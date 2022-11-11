import os.path
import subprocess


def parse(project, pwd_dir):
    print("[START JADX]")
    jadxbin = os.path.join(pwd_dir, "enhance", "bin", "jadx")
    apks_dir = project.apk_path
    cmd = jadxbin + " " + apks_dir + " -ds " + project.jadx_res
    #print("[CMD] : ", cmd)
    apkt_result = subprocess.check_output(cmd, shell=True)
    #print(apkt_result)
    # check r.id.java
    item = project.used_name.split(".")
    #print(item)
    dirs = project.jadx_res
    for name in item:
        dirs = os.path.join(dirs, name)
    dirs = os.path.join(dirs, "R.java")
    if not os.path.exists(dirs):
        print("R.java not exists!")
    else:
        print("R.java exists!")
        project.rjava_res = dirs