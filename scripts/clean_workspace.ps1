# Cleanup script - moves generated/unnecessary files to a timestamped backup folder
# Run from project root in PowerShell: .\scripts\clean_workspace.ps1

param(
    [switch]$Execute
)

$now = Get-Date -Format "yyyyMMdd_HHmmss"
$pwd = (Get-Location).Path
$backup = Join-Path -Path $pwd -ChildPath "cleanup_backup_$now"
Write-Host "Backup folder: $backup"

# List of candidate patterns (relative to repo root)
$patterns = @(
    "travelcompanion.apk",
    "*.apk",
    ".gradle",
    "build",
    "app/build",
    ".idea",
    ".vscode",
    ".kotlin",
    "local.properties"
)

# Collect existing items
$toMove = [System.Collections.Generic.List[string]]::new()
foreach ($p in $patterns) {
    # If pattern is a direct path and exists
    $full = Join-Path -Path $pwd -ChildPath $p
    if (Test-Path $full) {
        $toMove.Add((Resolve-Path $full).Path)
        continue
    }
    # Otherwise use Get-ChildItem with -Filter for simple patterns
    try {
        $matches = Get-ChildItem -Path $pwd -Recurse -Force -ErrorAction SilentlyContinue | Where-Object { $_.Name -like $p }
        foreach ($m in $matches) {
            $toMove.Add($m.FullName)
        }
    } catch {
        # ignore
    }
}

# Remove duplicates
$toMove = $toMove | Select-Object -Unique

if ($toMove.Count -eq 0) {
    Write-Host "No candidate files/folders found for cleanup."
    exit 0
}

Write-Host "Found $($toMove.Count) items to move:" -ForegroundColor Yellow
$toMove | ForEach-Object { Write-Host " - $_" }

if (-not $Execute) {
    Write-Host "\nDry run. To perform the move, re-run this script with -Execute" -ForegroundColor Cyan
    exit 0
}

# Create backup folder
New-Item -ItemType Directory -Path $backup | Out-Null

# Move items
$report = @()
foreach ($item in $toMove) {
    try {
        $leaf = Split-Path $item -Leaf
        $dest = Join-Path -Path $backup -ChildPath $leaf
        Move-Item -Path $item -Destination $dest -Force -ErrorAction Stop
        $report += $item
        Write-Host "Moved: $item -> $dest"
    } catch {
        Write-Host "Failed to move $item: $_" -ForegroundColor Red
    }
}

# Write CLEANUP_REPORT.md
$reportFile = Join-Path -Path $pwd -ChildPath "CLEANUP_REPORT_$now.md"
$reportContent = "# Cleanup report - $now`n`nThe following items were moved to the backup folder: $backup`n`nMoved items:`n" + ($report | ForEach-Object { "- $_" } | Out-String)
$reportContent | Out-File -FilePath $reportFile -Encoding UTF8

Write-Host "Cleanup complete. Report: $reportFile" -ForegroundColor Green
