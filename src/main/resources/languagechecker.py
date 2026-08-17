import json
import os

def load_json(filepath):
    try:
        with open(filepath, 'r', encoding='utf-8') as file:
            return json.load(file)
    except (FileNotFoundError, json.JSONDecodeError) as e:
        print(f"Error loading {filepath}: {e}")
        return {}

def get_all_keys(d, parent_key=''):
    """Recursively extract all keys from a nested dictionary."""
    keys = set()
    for key, value in d.items():
        full_key = f"{parent_key}->{key}" if parent_key else key
        keys.add(full_key)
        if isinstance(value, dict):
            keys.update(get_all_keys(value, full_key))
    return keys

def main():
    # File paths
    files = {
        "en": "languages/en.json",
        "nb-no": "languages/nb-no.json",
        "nn-no": "languages/nn-no.json"
    }

    # Load JSON data
    data = {lang: load_json(path) for lang, path in files.items()}

    # Extract keys
    keys = {lang: get_all_keys(content) for lang, content in data.items()}

    # Find missing keys
    all_keys = set.union(*keys.values())

    missing_keys = {}
    for lang, lang_keys in keys.items():
        missing_keys[lang] = all_keys - lang_keys

    # Print missing keys per language
    for lang, missing in missing_keys.items():
        if missing:
            print(f"\nKeys missing in {lang}:")
            for key in sorted(missing):
                print(key)

if __name__ == "__main__":
    main()
