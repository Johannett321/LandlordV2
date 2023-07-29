import json
import glob
import time

def deep_get(dic, keys):
    for key in keys:
        dic = dic.get(key, {})
    return dic

def deep_set(dic, keys, value):
    for key in keys[:-1]:
        if key not in dic:
            dic[key] = {}
        dic = dic[key]
    dic[keys[-1]] = value

def add_content_to_json(file_name, key, value):
    with open(file_name, 'r', encoding='utf-8') as f:
        data = json.load(f)

    keys = key.split('.')
    deep_set(data, keys, value)

    with open(file_name, 'w', encoding='utf-8') as f:
        json.dump(data, f, ensure_ascii=False, indent=4)

def navigate_keys(file_name):
    with open(file_name, 'r', encoding='utf-8') as f:
        data = json.load(f)

    keys = []
    while True:
        print("----------------------------------------------------------")
        path = " -> ".join(keys) if keys else "root"
        value = deep_get(data, keys)

        print(f"Current path: {path}")

        if isinstance(value, dict):
            print("######### Keys #########")
            print("\n".join(value.keys()))
        else:
            print(f"Current value: {value}")

        print("Enter a key to navigate to, 'up' to go up, 'add' to add a new key, or 'quit' to finish:")
        command = input("> ")

        if command == "quit":
            break
        elif command == "add":
            new_key = input("Enter the new key: ")
            keys.append(new_key)
            print(f"Adding to path: {' -> '.join(keys)}")
            for file in glob.glob('./languages/*.json'):
                new_value = input(f"Enter a value for '{file}': ")
                add_content_to_json(file, '.'.join(keys), new_value)
            print(f"Newly added key: {'.'.join(keys)}")
            input("[Press enter to continue]")
            keys.pop()
        elif command == "up":
            if keys:
                keys.pop()
        else:
            keys.append(command)

def main():
    navigate_keys('./languages/en.json')
    print("Content has been added successfully!")

if __name__ == "__main__":
    main()