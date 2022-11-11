import subprocess
import re

def pre_aapt(apkpath):
    defined_pkg_name = ""
    versionCode = ""
    launcher = ""
    used_pkg_name = ""
    aapt_result = ""

    # get aapt result
    cmd = "aapt dump badging " + apkpath
    aapt_result = subprocess.check_output(cmd, shell=True)
    aapt_result = str(aapt_result)

    # get defined_pkg_name
    searchObj = re.search(r'package: name=\'([\S]*)\'', aapt_result, re.M|re.I)
    if searchObj == None:
        print("[-] don't get defined pkg name")
        defined_pkg_name = ""
    else:
        defined_pkg_name = searchObj.group(1)
        print("[+] get defined pkg name: ", defined_pkg_name)

    # get versionCode
    searchObj = re.search(r'versionCode=\'([\S]*)\'', aapt_result, re.M | re.I)
    if searchObj == None:
        print("[-] don't get version Code")
        versionCode = ""
    else:
        versionCode = searchObj.group(1)
        print("[+] get version Code: ", versionCode)

    #get launcher
    searchObj = re.search(r'launchable-activity=\'([\S]*)\'', aapt_result, re.M | re.I)
    if searchObj == None:
        launcher = ""
        print("[-] don't get launcher")
    else:
        launcher = searchObj.group(1)
        print("[+] get launcher: ", launcher)

    # sometimes launcher is empty or launcher starts with "."
    if launcher == '' or defined_pkg_name in launcher or launcher.startswith("."):
        used_pkg_name = defined_pkg_name
    else:
        used_pkg_name = launcher.replace('.' + launcher.split('.')[-1], '').split('\'')[1]

    result = {}
    result['defined_pkg_name'] = defined_pkg_name
    result['used_pkg_name'] = used_pkg_name
    result['versionCode'] = versionCode

    return result

if __name__ == '__main__':
    apkpath = b"../testfile/draw.apk"
    pre_aapt(apkpath)