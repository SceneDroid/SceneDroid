import os
import time
from multiprocessing import Process
from devices_list import scan
from dymaic import run_apk
from mylog import wlog
from parseManifest import parseM
from parseCTG import paseCTG
from pret import aapt
from structure import project
from pret import apktool
from repkg import repkg
from enhance import iccbot, myjadx
from fuzz import buildscreen
from tools import transcreen

# config
result_folder = ""
apks_folder = ""
iccbot_dir = ""
device_model = 1  # 0: remote 1: local


def init_apk(apk_dir, apkname):
    print("------------------------------")
    print("[~] Start Run: ", apk_dir)
    print("------------------------------")

    # get aapt info
    aapt_info = aapt.pre_aapt(apk_dir)
    if aapt_info != {}:
        print("[+] Get AAPT Info!")
    else:
        print("[-] Don't get AAPT Info!")
        exit(0)
    # print(aapt_info)

    project_id = aapt_info['used_pkg_name'] + "-" + aapt_info['versionCode']
    print("[+] set project id: ", project_id)
    project_result_dir = os.path.join(result_folder, project_id)
    print("[+] set project result dir: ", project_result_dir)

    p = project.project(project_id, project_result_dir, aapt_info['versionCode'], aapt_info['used_pkg_name'], apk_dir)
    if p.p_id != "" and p.res_dir != "":
        print("[+] creat new project: ", project_id)
    else:
        print("[-] don't creat new project: ", project_id)
    p.apks_folder = apks_folder
    p.root_dir = os.getcwd()
    p.apk_name = apkname
    return p


if __name__ == '__main__':
    # init dir
    pwd_dir = os.getcwd()
    iccbot_dir = os.path.join(pwd_dir, "enhance", "icc")
    result_folder = os.path.join(pwd_dir, "result")
    apks_folder = os.path.join(pwd_dir, "apks")
    log_path = os.path.join(pwd_dir, "log.txt")
    success_list = os.path.join(pwd_dir, "success.txt")
    fault_list = os.path.join(pwd_dir, "fault.txt")
    wlog.init(log_path)
    with open(success_list, 'w') as f:
        f.close()
    with open(fault_list, 'w') as f:
        f.close()
    # 检查APK目录
    if not os.path.exists(apks_folder):
        strt = "[!] Not exists apks folder!"
        print(strt)
        wlog.wlog(strt)
        os.makedirs(apks_folder)
        exit(0)
    else:
        strt = "[+] Get apks folder: " + apks_folder
        print(strt)
        wlog.wlog(strt)

    # 建立工作目录
    if not os.path.exists(result_folder):
        os.makedirs(result_folder)
        strt = "[+] Mkdir new result folder: " + result_folder
        print(strt)
        wlog.wlog(strt)
    else:
        strt = "[+] Get result folder: " + result_folder
        print(strt)
        wlog.wlog(strt)

    # 获取APK列表
    apks = []
    index = 1
    for apk in os.listdir(apks_folder):
        apks.append(apk)
        strt = "[+] find " + str(index) + " : " + apk
        print(strt)
        wlog.wlog(strt)
        index = index + 1
    if index > 1:
        strt = "[+] Total apks: " + str(index - 1)
        print(strt)
        wlog.wlog(strt)
    else:
        strt = "[-] None apks in " + apks_folder
        print(strt)
        wlog.wlog(strt)
        exit(0)

    # init project list
    project_list = []
    for apk in apks:
        apk_dir = os.path.join(apks_folder, apk)
        try:
            project_list.append(init_apk(apk_dir, apk))
        except:
            try:
                os.remove(apk_dir)
            except:
                pass

    # 初始化包分类
    pkg_up_list = {}
    for p in project_list:
        pkg_up_list[p.used_name] = []
    print("[+] Build pkg_up_list: ", pkg_up_list)

    # apktools unpack apk
    for p in project_list:
        try:
            apktool.unpackAPK(p)
        except:
            project_list.remove(p)

    # 重打包
    for p in project_list:
        try:
            repkg.main(p)
        except:
            project_list.remove(p)

    # check unpack info
    for p in project_list:
        p.printAll()

    # init iccbot
    for p in project_list:
        # pass
        try:
            iccbot.init(p, iccbot_dir, pwd_dir)
            p.initicc()
            if not os.path.exists(p.iccobj.root_dir):
                print("[-] root dir is not exists")
                wlog.wlog("[-] root dir is not exists")
                # continue
            if not os.path.exists(p.iccobj.callgraph):
                print("[-] CallGraphInfo dir is not exists")
                wlog.wlog("[-] CallGraphInfo dir is not exists")
                # continue
            if not os.path.exists(p.iccobj.ctg):
                print("[-] CTGResult dir is not exists")
                wlog.wlog("[-] CTGResult dir is not exists")
                # continue
            if not os.path.exists(p.iccobj.fragment):
                print("[-] FragmentInfo dir is not exists")
                wlog.wlog("[-] FragmentInfo dir is not exists")
                # continue
            if not os.path.exists(p.iccobj.iccsep):
                print("[-] ICCSpecification dir is not exists")
                wlog.wlog("[-] ICCSpecification dir is not exists")
                # continue
            if not os.path.exists(p.iccobj.manifest):
                print("[-] ManifestInfo dir is not exists")
                wlog.wlog("[-] ManifestInfo dir is not exists")
                # continue
            if not os.path.exists(p.iccobj.soot):
                print("[-] SootIRInfo dir is not exists")
                wlog.wlog("[-] SootIRInfo dir is not exists")
                # continue
        except:
            pass
            # project_list.remove(p)

    # check unpack info
    for p in project_list:
        p.printAll()

    for p in project_list:
        try:
            myjadx.parse(p, pwd_dir)
        except:
            project_list.remove(p)


    # get widget id
    for p in project_list:
        try:
            p.entrances = paseCTG.parseCTG(p)
            print(p.entrances)
        except:
            print("get widget id False")

    # parseManifest
    for p in project_list:
        try:
            parse_result = parseM.parseManifest(p)

            with open(os.path.join(p.res_dir, "actnum.txt"), "w") as f:
                f.writelines(str(p.actnum) + "\n")

            print()
            if parse_result != {}:
                print("[+] get parseManifest!")
            else:
                print("[-] don't get parseManifest!")
                exit(0)
            # show parse result
            p.setParse(parse_result)
            parseStr = []
            # 初始化Activiy列表
            actlist = []
            for act in parse_result:
                if act not in actlist:
                    actlist.append(act)
            for act in parse_result:
                parseStr.append("==")
                parseStr.append("Activity: " + act)
                for intent in parse_result[act]:
                    parseStr.append("[Action]: " + intent[0])
                    parseStr.append("[Category]: " + intent[1])
            p.setAct(actlist)
            p.printAll()
            parseManifest_path = os.path.join(p.res_dir, "parseManifest.txt")
            # clear parseManifest
            with open(parseManifest_path, 'w') as f:
                f.close()
            # write parseManifest
            with open(parseManifest_path, 'a') as f:
                for index in parseStr:
                    f.writelines(index + "\n")
            print("[+] Write to parseManifest.txt: ", parseManifest_path)
        except:
            project_list.remove(p)

    phone_list = scan.scan_devices(device_model)
    if phone_list:
        print("[+] get Phone list: ", phone_list)
    else:
        print("[-] None Phone list!")
        exit(0)

    suceess_project = []
    fault_project = []
    # start dynamic
    for p in project_list:
        time.sleep(1)
        # run_apk.run(p, phone_list[0])
        try:
            run_apk.run(p, phone_list[0])
            try:
                transcreen.parsetrans(p)
            except:
                pass
            try:
                p.printTrans()
            except:
                pass
            try:
                p.savegv()
            except:
                pass
        except:
            phone_list[0].uiauto.app_stop(p.used_name)
            # phone_list[0].uiauto.app_uninstall(p.used_name)
            continue
        phone_list[0].uiauto.app_stop(p.used_name)
        phone_list[0].uiauto.app_uninstall(p.used_name)
        # os.remove(p.apk_path)
        # 卸载并清理环境
