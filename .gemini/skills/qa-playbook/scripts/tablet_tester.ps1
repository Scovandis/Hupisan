<#
.SYNOPSIS
    Script helper otomatisasi QA untuk pengujian langsung pada Tablet / Perangkat Android via ADB.
.DESCRIPTION
    Menjalankan berbagai aksi pengujian QA seperti membuka aplikasi, mengambil screenshot bukti,
    inspeksi layout UI, interaksi (tap, swipe, input teks, tombol fisik), dan pemantauan crash/logcat.
.EXAMPLE
    .\tablet_tester.ps1 -Action check-device
    .\tablet_tester.ps1 -Action specs
    .\tablet_tester.ps1 -Action launch -Package "my.notes.HHHH"
    .\tablet_tester.ps1 -Action screenshot -OutputFile "bukti_home.png"
    .\tablet_tester.ps1 -Action tap -X 500 -Y 800
    .\tablet_tester.ps1 -Action type -Text "Catatan%sTest"
    .\tablet_tester.ps1 -Action get-logs -OutputFile "crash_log.txt"
#>

[CmdletBinding()]
param (
    [Parameter(Mandatory = $true)]
    [ValidateSet("check-device", "specs", "launch", "stop", "restart", "clear-data", "screenshot", "dump-ui", "tap", "swipe", "type", "key", "current-activity", "get-logs", "clear-logs", "custom")]
    [string]$Action,

    [string]$Package = "my.notes.HHHH",
    [string]$Activity = "my.notes.HHHH.view.spalshscreen.SplashScreenActivity",
    [string]$DeviceSerial = "",
    [string]$OutputFile = "",
    [int]$X = 0,
    [int]$Y = 0,
    [int]$X1 = 0,
    [int]$Y1 = 0,
    [int]$X2 = 0,
    [int]$Y2 = 0,
    [int]$DurationMs = 400,
    [string]$Text = "",
    [int]$KeyCode = 4, # Default 4 = BACK
    [string]$CustomCommand = ""
)

# 1. Lokasi ADB
function Get-AdbPath {
    $candidates = @(
        "adb",
        "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe",
        "C:\Users\andre\AppData\Local\Android\Sdk\platform-tools\adb.exe",
        "$env:ANDROID_HOME\platform-tools\adb.exe",
        "$env:ANDROID_SDK_ROOT\platform-tools\adb.exe"
    )

    foreach ($path in $candidates) {
        if ($path -eq "adb") {
            $cmd = Get-Command "adb" -ErrorAction SilentlyContinue
            if ($cmd) { return $cmd.Source }
        } elseif (Test-Path $path) {
            return $path
        }
    }
    throw "ADB tidak ditemukan. Pastikan Android SDK platform-tools terinstall."
}

$Adb = Get-AdbPath

function Invoke-AdbArgs {
    param([string[]]$ArgsList)
    $cmdArgs = @()
    if ($DeviceSerial) {
        $cmdArgs += @("-s", $DeviceSerial)
    }
    $cmdArgs += $ArgsList
    & $Adb @cmdArgs
}

switch ($Action) {
    "check-device" {
        Write-Host "=== STATUS KONEKSI ADB & PERANGKAT ===" -ForegroundColor Cyan
        & $Adb devices -l
    }

    "specs" {
        Write-Host "=== SPESIFIKASI TABLET / PERANGKAT ===" -ForegroundColor Cyan
        $model = Invoke-AdbArgs @("shell", "getprop", "ro.product.model")
        $manufacturer = Invoke-AdbArgs @("shell", "getprop", "ro.product.manufacturer")
        $androidVer = Invoke-AdbArgs @("shell", "getprop", "ro.build.version.release")
        $sdkVer = Invoke-AdbArgs @("shell", "getprop", "ro.build.version.sdk")
        $screenSize = Invoke-AdbArgs @("shell", "wm", "size")
        $density = Invoke-AdbArgs @("shell", "wm", "density")

        Write-Host "Manufacturer   : $manufacturer"
        Write-Host "Model          : $model"
        Write-Host "Android Version: $androidVer (API $sdkVer)"
        Write-Host "Screen Size    : $screenSize"
        Write-Host "Display Density: $density"
    }

    "launch" {
        Write-Host "Membuka aplikasi: $Package / $Activity..." -ForegroundColor Green
        # Coba buka activity spesifik, fallback ke launcher monkey jika gagal
        $out = Invoke-AdbArgs @("shell", "am", "start", "-n", "$Package/$Activity")
        Write-Host $out
        Start-Sleep -Seconds 2
        Write-Host "Activity aktif saat ini:" -ForegroundColor Yellow
        Invoke-AdbArgs @("shell", "dumpsys", "window", "windows") | Select-String "mCurrentFocus"
    }

    "stop" {
        Write-Host "Menghentikan aplikasi: $Package..." -ForegroundColor Yellow
        Invoke-AdbArgs @("shell", "am", "force-stop", $Package)
        Write-Host "Aplikasi telah dihentikan."
    }

    "restart" {
        Write-Host "Me-restart aplikasi: $Package..." -ForegroundColor Yellow
        Invoke-AdbArgs @("shell", "am", "force-stop", $Package)
        Start-Sleep -Seconds 1
        Invoke-AdbArgs @("shell", "am", "start", "-n", "$Package/$Activity")
        Start-Sleep -Seconds 2
        Write-Host "Aplikasi berhasil di-restart."
    }

    "clear-data" {
        Write-Host "Mereset data & cache aplikasi: $Package..." -ForegroundColor Yellow
        Invoke-AdbArgs @("shell", "pm", "clear", $Package)
    }

    "screenshot" {
        if (-not $OutputFile) {
            $timestamp = Get-Date -Format "yyyyMMdd_HHmmss"
            $OutputFile = "screenshot_$timestamp.png"
        }
        Write-Host "Mengambil screenshot perangkat ke: $OutputFile..." -ForegroundColor Cyan
        Invoke-AdbArgs @("shell", "screencap", "-p", "/sdcard/qa_screen.png") | Out-Null
        Invoke-AdbArgs @("pull", "/sdcard/qa_screen.png", $OutputFile) | Out-Null
        Invoke-AdbArgs @("shell", "rm", "/sdcard/qa_screen.png") | Out-Null
        if (Test-Path $OutputFile) {
            $fileInfo = Get-Item $OutputFile
            Write-Host "Screenshot berhasil disimpan ($($fileInfo.Length) bytes)." -ForegroundColor Green
        } else {
            Write-Error "Gagal menyimpan screenshot."
        }
    }

    "dump-ui" {
        if (-not $OutputFile) {
            $timestamp = Get-Date -Format "yyyyMMdd_HHmmss"
            $OutputFile = "ui_dump_$timestamp.xml"
        }
        Write-Host "Mengambil UI Hierarchy dump..." -ForegroundColor Cyan
        Invoke-AdbArgs @("shell", "uiautomator", "dump", "/data/local/tmp/window_dump.xml") | Out-Null
        Invoke-AdbArgs @("pull", "/data/local/tmp/window_dump.xml", $OutputFile) | Out-Null
        if (Test-Path $OutputFile) {
            Write-Host "UI Hierarchy berhasil disimpan di: $OutputFile" -ForegroundColor Green
        } else {
            Write-Error "Gagal menarik UI hierarchy dump."
        }
    }

    "tap" {
        Write-Host "Mengirim aksi TAP ke koordinat: ($X, $Y)..." -ForegroundColor Cyan
        Invoke-AdbArgs @("shell", "input", "tap", $X, $Y)
    }

    "swipe" {
        Write-Host "Mengirim aksi SWIPE dari ($X1, $Y1) ke ($X2, $Y2) durasi ${DurationMs}ms..." -ForegroundColor Cyan
        Invoke-AdbArgs @("shell", "input", "swipe", $X1, $Y1, $X2, $Y2, $DurationMs)
    }

    "type" {
        Write-Host "Mengirim teks ke field fokus: $Text" -ForegroundColor Cyan
        # Ganti spasi dengan %s untuk ADB shell input
        $sanitizedText = $Text -replace " ", "%s"
        Invoke-AdbArgs @("shell", "input", "text", "`"$sanitizedText`"")
    }

    "key" {
        Write-Host "Mengirim keyevent: $KeyCode..." -ForegroundColor Cyan
        Invoke-AdbArgs @("shell", "input", "keyevent", $KeyCode)
    }

    "current-activity" {
        Write-Host "Screen / Activity yang sedang fokus:" -ForegroundColor Cyan
        Invoke-AdbArgs @("shell", "dumpsys", "window", "windows") | Select-String "mCurrentFocus|mFocusedApp"
    }

    "get-logs" {
        if (-not $OutputFile) {
            $timestamp = Get-Date -Format "yyyyMMdd_HHmmss"
            $OutputFile = "logcat_$timestamp.txt"
        }
        Write-Host "Menyimpan logcat error/warning ke: $OutputFile..." -ForegroundColor Cyan
        & $Adb logcat -d -v time *:E > $OutputFile
        Write-Host "Log berhasil diekstrak." -ForegroundColor Green
    }

    "clear-logs" {
        Write-Host "Membersihkan buffer logcat..." -ForegroundColor Cyan
        Invoke-AdbArgs @("logcat", "-c")
        Write-Host "Buffer logcat bersih." -ForegroundColor Green
    }

    "custom" {
        if (-not $CustomCommand) {
            Write-Error "Harap sertakan parameter -CustomCommand"
            return
        }
        Write-Host "Menjalankan perintah custom ADB: adb $CustomCommand" -ForegroundColor Cyan
        Invoke-Expression "& `"$Adb`" $CustomCommand"
    }
}
