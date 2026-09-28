import os

# Root directory to scan (current directory)
ROOT_DIR = "."
OUTPUT_FILE = "project_data.txt"

# Folders to skip entirely
IGNORE_DIRS = {"node_modules", ".git", "__pycache__", ".next", "dist", "build", ".venv", "venv"}

# File extensions to skip (optional — remove if you want everything)
# IGNORE_EXTS = {".png", ".jpg", ".jpeg", ".gif", ".ico", ".lock", ".log"}
IGNORE_EXTS = set()


def should_skip_dir(dirname):
    return dirname in IGNORE_DIRS or dirname.startswith(".")


def is_binary(filepath):
    """Quick check: read first 1KB and look for null bytes."""
    try:
        with open(filepath, "rb") as f:
            chunk = f.read(1024)
        return b"\x00" in chunk
    except Exception:
        return True


def main():
    with open(OUTPUT_FILE, "w", encoding="utf-8") as out:
        for current_dir, subdirs, files in os.walk(ROOT_DIR):
            # Mutate subdirs in-place so os.walk skips them
            subdirs[:] = [d for d in subdirs if not should_skip_dir(d)]

            for filename in files:
                ext = os.path.splitext(filename)[1].lower()
                if ext in IGNORE_EXTS:
                    continue

                full_path = os.path.join(current_dir, filename)
                rel_path = os.path.relpath(full_path, ROOT_DIR).replace(os.sep, "/")

                if is_binary(full_path):
                    continue

                try:
                    with open(full_path, "r", encoding="utf-8") as f:
                        content = f.read()
                except (UnicodeDecodeError, PermissionError):
                    continue

                out.write(f"this is my {rel_path}:\n")
                out.write(content)
                if not content.endswith("\n"):
                    out.write("\n")
                out.write("\n")  # blank line between files

    print(f"Done. Wrote {OUTPUT_FILE}")


if __name__ == "__main__":
    main()
