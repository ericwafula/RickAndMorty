#!/usr/bin/env bash
#
# Sync .agents/ into .claude/ as symlinks so Claude Code can discover them, while
# .agents/ stays the source of truth:
#   .agents/skills/<area>/<skill>/  ->  .claude/skills/<skill>
#   .agents/agents/<name>.md        ->  .claude/agents/<name>.md
#
# Each skill is a directory containing a SKILL.md, laid out as:
#   .agents/skills/<module>/<skill-name>/SKILL.md
# and exposed to Claude Code as:
#   .claude/skills/<skill-name> -> ../../.agents/skills/<module>/<skill-name>
#
# Re-runnable: removes only the symlinks it manages, then recreates them.
# Run from anywhere; paths are resolved relative to the repo root.

set -euo pipefail

# Repo root = parent of this script's .agents directory.
script_dir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
repo_root="$(cd "$script_dir/.." && pwd)"

src_root="$repo_root/.agents/skills"
dest_root="$repo_root/.claude/skills"

if [[ ! -d "$src_root" ]]; then
    echo "No skills found at $src_root — nothing to sync." >&2
    exit 0
fi

mkdir -p "$dest_root"

# Drop existing managed symlinks so renamed/removed skills don't linger.
# Only touches symlinks, never real files or directories.
for entry in "$dest_root"/*; do
    [[ -L "$entry" ]] && rm "$entry"
done

shopt -s nullglob
linked=0
seen=" "  # space-delimited list of already-linked skill names (bash 3.2 friendly)

for skill_md in "$src_root"/*/*/SKILL.md; do
    skill_dir="$(dirname "$skill_md")"
    name="$(basename "$skill_dir")"
    module="$(basename "$(dirname "$skill_dir")")"

    if [[ "$seen" == *" $name "* ]]; then
        echo "WARN: skill name '$name' (in module '$module') collides with an earlier module — skipping." >&2
        continue
    fi
    seen="$seen$name "

    # Symlink lives at .claude/skills/<name>; target is two levels up into .agents.
    ln -s "../../.agents/skills/$module/$name" "$dest_root/$name"
    echo "linked: $name  ($module)"
    linked=$((linked + 1))
done

echo "Synced $linked skill(s) into $dest_root"

# Agents: .agents/agents/<name>.md -> .claude/agents/<name>.md
agents_src="$repo_root/.agents/agents"
agents_dest="$repo_root/.claude/agents"
if [[ -d "$agents_src" ]]; then
    mkdir -p "$agents_dest"
    for entry in "$agents_dest"/*; do
        [[ -L "$entry" ]] && rm "$entry"
    done
    agents_linked=0
    for agent_md in "$agents_src"/*.md; do
        name="$(basename "$agent_md")"
        ln -s "../../.agents/agents/$name" "$agents_dest/$name"
        echo "linked agent: ${name%.md}"
        agents_linked=$((agents_linked + 1))
    done
    echo "Synced $agents_linked agent(s) into $agents_dest"
fi
