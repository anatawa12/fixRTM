#!/bin/bash

set -eu

# utilities
function get_latest_release() {
  curl -H "Authorization: token $GITHUB_TOKEN" "https://api.github.com/repos/$1/releases/latest" |
    jq -r '.tag_name'
}

# 仮ファイルの準備

function rm_tmpfile {
  [[ -f "$1" ]] && rm -f "$1"
}

function make_temp() {
  local tmp_file_1
  tmp_file_1=$(mktemp)

  # shellcheck disable=SC2064
  trap "rm_tmpfile '$tmp_file_1'" EXIT
  # shellcheck disable=SC2064
  trap "trap - EXIT; rm_tmpfile '$tmp_file_1'; exit -1" INT PIPE TERM

  echo "$tmp_file_1"
}

tmp_file=$(make_temp)

# バージョン情報を作成
if [ "$VERSION_NAME_IN" == "snapshot-generated" ]; then
    version_name="$(date "+SNAPSHOT-%Y-%m-%d-%H-%M-%S")"
    prerelease=true
    nightly=false
elif [ "$VERSION_NAME_IN" == "" ]; then
    source "$(dirname "$0")/check_nightly_functions.sh"
    if ! need_nightly_build; then
      cancel
      exit 0;
    fi
    version_name="$(date "+SNAPSHOT-%Y-%m-%d-NIGHTLY")"
    prerelease=true
    nightly=true
else
    version_name="$VERSION_NAME_IN"
    prerelease=false
    nightly=false
fi

# バージョン情報をverify

if echo "$version_name" | grep -Eq -v '^SNAPSHOT-[0-9-]+(-NIGHTLY)?$|^[0-9.]+$' > /dev/null; then
  echo "invalid version name: $version_name"
  exit 255
fi

# 各ファイルのバージョン情報更新

if [ "$prerelease" != "true" ]; then
  # update version map

  # masterの行を複製してmasterをバージョン名で置き換える
  cat version-map.md > "$tmp_file"
  awk '{ if (match($0, "master")) { printf("%s%-14s |\n", substr($0, 0, 33), "'"$version_name"'") } ; print }' "$tmp_file" > version-map.md
  rm "$tmp_file"
fi

required_rtm="$(grep master version-map.md | cut -d '|' -f 2 | sed -E 's/^ +| +$//')"
required_ngtlib="$(grep master version-map.md | cut -d '|' -f 3 | sed -E 's/^ +| +$//')"

# gradle.properties を更新
cat gradle.properties > "$tmp_file"
sed 's/^modVersion=.*/modVersion='"$version_name"'/' "$tmp_file" > gradle.properties
rm "$tmp_file"

# コミットとリリース情報を追加

if [ "$prerelease" != "true" ]; then
  changelog_tag_pattern='^[\d.]+$'
  changelog_file_path='CHANGELOG.md'
else
  changelog_tag_pattern='^SNAPSHOT-[0-9-]+$|^[\d.]+$'
  changelog_file_path='CHANGELOG-SNAPSHOTS.md'
fi

# first commit
git commit -am "$version_name"
git tag "$version_name"

# make release notes commit
auto-changelog --commit-limit 0 \
    --sort-tags=-committerdate \
    --tag-pattern "$changelog_tag_pattern"\
    -o "$changelog_file_path"

git reset --soft HEAD^
if [ "$nightly" != "true" ]; then
# re-commit to add updated changelog
git tag -d "$version_name"
git commit -am "$version_name"
git tag "$version_name"
fi

# release noteの抽出

release_note_path=$(mktemp)
{
if [ "$prerelease" == "true" ]; then
  echo "**This is SNAPSHOT, not a stable release. make sure this may have many bugs.**"
fi
echo "Requirements for this version: "
echo "RTM $required_rtm"
echo "NGTLib $required_ngtlib"
echo ""
echo "This changelog was generated by [my fork of auto-changelog](https://github.com/anatawa12/auto-changelog)."
echo ""
awk "$(sed "s/{MATCH}/[$version_name]/" scripts/extract-changelog.awk)" "$changelog_file_path"
} >> "$release_note_path"

# 出力設定

asset_path="./build/libs/fixRtm-$version_name.jar"
asset_name="fixRtm-$version_name.jar"

echo "::set-output name=release_note_path::$release_note_path"
echo "::set-output name=version_name::$version_name"
echo "::set-output name=prerelease::$prerelease"
echo "::set-output name=asset_path::$asset_path"
echo "::set-output name=asset_name::$asset_name"
echo "::set-output name=required_rtm::$required_rtm"
echo "::set-output name=required_ngtlib::$required_ngtlib"
echo "::set-output name=nightly::$nightly"
