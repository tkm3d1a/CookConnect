#!/bin/bash

# Semantic Version Validation Script
# Usage: ./validate-semver.sh <main_version> <pr_version> <service_name>

set -e

MAIN_VER=$1
PR_VER=$2
SERVICE=$3

if [ -z "$MAIN_VER" ] || [ -z "$PR_VER" ] || [ -z "$SERVICE" ]; then
    echo "Usage: $0 <main_version> <pr_version> <service_name>"
    exit 1
fi

echo "=== Validating Version for $SERVICE ==="
echo "Main branch: $MAIN_VER"
echo "PR branch:   $PR_VER"
echo ""

# Validate format X.Y.Z
if [[ ! "$PR_VER" =~ ^[0-9]+\.[0-9]+\.[0-9]+$ ]]; then
    echo "❌ Invalid version format: $PR_VER"
    echo "   Expected format: X.Y.Z (e.g., 0.1.0, 1.2.3)"
    exit 1
fi

echo "✅ Version format valid"

# Parse versions
IFS='.' read -r main_x main_y main_z <<< "$MAIN_VER"
IFS='.' read -r pr_x pr_y pr_z <<< "$PR_VER"

# Check if version increased
if [ "$PR_VER" == "$MAIN_VER" ]; then
    echo "❌ Version unchanged: $MAIN_VER"
    echo "   Please increment the version"
    exit 1
fi

# Validate increment rules
if [ $pr_x -gt $main_x ]; then
    # Major version increment
    echo "🔍 Validating major version increment..."

    if [ $((pr_x - main_x)) -ne 1 ]; then
        echo "❌ Major version must increment by exactly 1"
        echo "   Got: $main_x → $pr_x (difference: $((pr_x - main_x)))"
        exit 1
    fi

    if [ $pr_y -ne 0 ] || [ $pr_z -ne 0 ]; then
        echo "❌ Major version increment must reset minor and patch to 0"
        echo "   Expected: $pr_x.0.0"
        echo "   Got:      $pr_x.$pr_y.$pr_z"
        exit 1
    fi

    echo "✅ Valid major version increment: $MAIN_VER → $PR_VER"
    exit 0

elif [ $pr_y -gt $main_y ]; then
    # Minor version increment
    echo "🔍 Validating minor version increment..."

    if [ $pr_x -ne $main_x ]; then
        echo "❌ Major version should not change with minor increment"
        exit 1
    fi

    if [ $((pr_y - main_y)) -ne 1 ]; then
        echo "❌ Minor version must increment by exactly 1"
        echo "   Got: $main_y → $pr_y (difference: $((pr_y - main_y)))"
        exit 1
    fi

    if [ $pr_z -ne 0 ]; then
        echo "❌ Minor version increment must reset patch to 0"
        echo "   Expected: $pr_x.$pr_y.0"
        echo "   Got:      $pr_x.$pr_y.$pr_z"
        exit 1
    fi

    echo "✅ Valid minor version increment: $MAIN_VER → $PR_VER"
    exit 0

elif [ $pr_z -gt $main_z ]; then
    # Patch version increment
    echo "🔍 Validating patch version increment..."

    if [ $pr_x -ne $main_x ] || [ $pr_y -ne $main_y ]; then
        echo "❌ Major/minor versions should not change with patch increment"
        exit 1
    fi

    if [ $((pr_z - main_z)) -ne 1 ]; then
        echo "❌ Patch version must increment by exactly 1"
        echo "   Got: $main_z → $pr_z (difference: $((pr_z - main_z)))"
        exit 1
    fi

    echo "✅ Valid patch version increment: $MAIN_VER → $PR_VER"
    exit 0

else
    echo "❌ Version must be incremented"
    echo "   Main: $MAIN_VER"
    echo "   PR:   $PR_VER"
    echo "   Increment one of: major (X), minor (Y), or patch (Z)"
    exit 1
fi
