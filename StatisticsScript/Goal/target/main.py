import os
from subprocess import run

apks_dir = "./apks"
result_dir = "./res"
apk_list = []


if __name__ == '__main__':
    index = 1
    for apk in os.listdir(apks_dir):
        apk_list.append(apk)
        strt = "[+] find " + str(index) + " : " + apk
        print(strt)
        index = index + 1

    while len(apk_list) != 0:
        apk = apk_list.pop()
        real_path = os.path.join(apks_dir, apk)
        print("[+] ", real_path)
        cmd = "java -jar ./GoalExplorer-1.2-SNAPSHOT-jar-with-dependencies.jar ge -i "
        cmd = cmd + real_path + " -o " + result_dir + " -s /home/syc/Android/Sdk -d"
        run(cmd, shell=True)