import os
from shutil import copy

result_dir = "./res"
apks_dir = "./apks"
check_list = "./checklist"

file_list = []

if __name__ == '__main__':
    for check in os.listdir(check_list):
        file_list.append(check)
        print(check)

    for file in file_list:
        apk_path = os.path.join(check_list, file, "unpack", "dist")
        if os.path.exists(apk_path):
            print("[+]", apk_path)
            for apk in os.listdir(apk_path):
                if not "sign" in apk:
                    print(apk)
                    real_apk_path = os.path.join(apk_path, apk)
                    print("[REAL] : ", real_apk_path)
                    copy(real_apk_path, apks_dir)
                    print("[+] Successful Copy!")
