Get-ChildItem -Recurse -Filter *.java src\main\java | ForEach-Object { $_.FullName } > sources.txt
javac -d out -cp "lib\*;." $(Get-Content sources.txt)