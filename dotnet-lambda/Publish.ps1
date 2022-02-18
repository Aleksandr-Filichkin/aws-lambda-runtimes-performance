function Test-Project {
    return Test-Path .\DotNetFunction.csproj
}

function Publish-Project {
    dotnet publish -p:PublishProfile=.\Properties\PublishProfiles\Linux-x64.pubxml
    Write-Host
    Write-Host "Output files are at $(Get-Location)\bin\Release\net5.0\publish\linux-x64"
}

if(Test-Path .\src\DotNetFunction) {
    Set-Location src\DotnetFunction
    if(Test-Project) {
        Publish-Project
    }
} else {
    $cwd = (Get-Location).Path

    if($cwd -match "\\DotNetFunction") {
        if(Test-Project) {
            Publish-Project
        }
    } else {
        Write-Error "Cannot locate DotNetFunction."
    }
}