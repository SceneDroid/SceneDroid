import os
import subprocess

# Unpacking APKs with APKTOOLS
def unpackAPK(project):
    unpack_dir = os.path.join(project.res_dir, "unpack")
    if not os.path.exists(unpack_dir):
        os.makedirs(unpack_dir)
    else:
        flag = 0
        for file in os.listdir(unpack_dir):
            if "AndroidManifest.xml" in file:
                flag = 1
        if flag == 1:
            print("[+] Apktools has already performed the unpacking")
            project.setUnpack(unpack_dir)
            return

    project.setUnpack(unpack_dir)

    cmd = "apktool empty-framework-dir d -f -o " + unpack_dir + " " + project.apk_path

    apkt_result = subprocess.check_output(cmd, shell=True)
    apkt_result = str(apkt_result)
    print(apkt_result)

    flag = 0
    for file in os.listdir(unpack_dir):
        if "AndroidManifest.xml" in file:
            flag = 1

    if flag == 1:
        print("[+] Apktools unpack successful")
    else:
        print("[-] Apktools unpack fault")
        exit(0)

