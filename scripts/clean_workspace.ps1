# Cleanup script - moves generated/unnecessary files to a timestamped backup folder
# Run from project root in PowerShell: .\scripts\clean_workspace.ps1

param(
    [switch]$Execute,
    [switch]$ArchiveOnly  # se presente, sposta i file in archive_YYYY... invece di cancellare
)

$now = Get-Date -Format "yyyyMMdd_HHmmss"
$pwd = (Get-Location).Path
$backup = Join-Path -Path $pwd -ChildPath "cleanup_backup_$now"
$archive = Join-Path -Path $pwd -ChildPath "archive_$now"
Write-Host "Backup folder: $backup"
Write-Host "Archive folder: $archive"

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
    "local.properties",
    "removed_assets_backup",
    "removed_code_backup",
    "all_res.txt",
    "unused_res.txt",
    "refs.txt",
    "kotlin_symbol_refs.txt",
    "kotlin_unused_candidates.txt",
    "final_non_source_backup.zip"
)

# Protect gradle wrapper files (do not move/delete)
$protect = @(
    (Join-Path -Path $pwd -ChildPath "gradlew"),
    (Join-Path -Path $pwd -ChildPath "gradlew.bat"),
    (Join-Path -Path $pwd -ChildPath "gradle\\wrapper\\gradle-wrapper.jar"),
    (Join-Path -Path $pwd -ChildPath "gradle\\wrapper\\gradle-wrapper.properties")
)

# Collect existing items
$toMove = [System.Collections.Generic.List[string]]::new()
foreach ($p in $patterns) {
    # If pattern is a direct path and exists
    $full = Join-Path -Path $pwd -ChildPath $p
    if (Test-Path $full) {
        $resolved = (Resolve-Path $full).Path
        if ($protect -contains $resolved) { continue }
        $toMove.Add($resolved)
        continue
    }
    # Otherwise search for matching names (shallow)
    try {
        $matches = Get-ChildItem -Path $pwd -Recurse -Force -ErrorAction SilentlyContinue | Where-Object { $_.Name -like $p }
        foreach ($m in $matches) {
            $r = $m.FullName
            if ($protect -contains $r) { continue }
            $toMove.Add($r)
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
    Write-Host "`nDry run. To perform the move, re-run this script with -Execute or with -Execute -ArchiveOnly" -ForegroundColor Cyan
    Write-Host "You can also run Remove-Item with -WhatIf for a safe preview." -ForegroundColor Cyan
    exit 0
}

if ($ArchiveOnly) {
    New-Item -ItemType Directory -Path $archive -Force | Out-Null
    $targetFolder = $archive
} else {
    New-Item -ItemType Directory -Path $backup -Force | Out-Null
    $targetFolder = $backup
}

# Move items
$report = @()
foreach ($item in $toMove) {
    try {
        $leaf = Split-Path $item -Leaf
        $dest = Join-Path -Path $targetFolder -ChildPath $leaf
        Move-Item -Path $item -Destination $dest -Force -ErrorAction Stop
        $report += $item
        Write-Host "Moved: $item -> $dest"
    } catch {
        Write-Host ("Failed to move {0}: {1}" -f $item, $_) -ForegroundColor Red
    }
}

# Write CLEANUP_REPORT.md
$reportFile = Join-Path -Path $pwd -ChildPath "CLEANUP_REPORT_$now.md"
$reportContent = "# Cleanup report - $now`n`nThe following items were moved to the backup folder: $targetFolder`n`nMoved items:`n" + ($report | ForEach-Object { "- $_" } | Out-String)
$reportContent | Out-File -FilePath $reportFile -Encoding UTF8

Write-Host "Cleanup complete. Report: $reportFile" -ForegroundColor Green
