# Configuration
$keycloakUrl = "http://localhost:8074"  # No /auth here
$realm = "tkforgeworks"
$adminUser = "cc_admin"
$adminPassword = "admin"

# Get access token
$body = @{
    client_id = "admin-cli"
    username = $adminUser
    password = $adminPassword
    grant_type = "password"
    scope = "openid"
}

$tokenResponse = Invoke-RestMethod -Uri "$keycloakUrl/realms/$realm/protocol/openid-connect/token" `
    -Method Post `
    -ContentType "application/x-www-form-urlencoded" `
    -Body $body

$token = $tokenResponse.access_token
$headers = @{
    Authorization = "Bearer $token"
    "Content-Type" = "application/json"
}

# Fetch all users with pagination
$allUsers = @()
$first = 0
$max = 100

Write-Host "Fetching users..."

do {
    $users = Invoke-RestMethod -Uri "$keycloakUrl/admin/realms/$realm/users?username=gatling&first=$first&max=$max" `
        -Method Get `
        -Headers $headers

    if ($users.Count -gt 0) {
        Write-Host "Fetched $($users.Count) users (offset: $first)"
        $allUsers += $users
        $first += $max
    }
} while ($users.Count -eq $max)

Write-Host "Total users fetched: $($allUsers.Count)"

# Filter and delete users whose username starts with "gatling"
$deletedCount = 0
foreach ($user in $allUsers) {
    if ($user.username -like "gatling*") {
        Write-Host "Deleting user: $($user.username) (ID: $($user.id))"

        try {
            Invoke-RestMethod -Uri "$keycloakUrl/admin/realms/$realm/users/$($user.id)" `
                -Method Delete `
                -Headers $headers

            $deletedCount++
        }
        catch {
            Write-Host "Failed to delete user $($user.username): $_" -ForegroundColor Red
        }
    }
}

Write-Host "`nDeleted $deletedCount users" -ForegroundColor Green