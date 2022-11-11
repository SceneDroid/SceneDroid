import subprocess
import time


# 恢复待测场景
def goback(device, screen_obj):
    """
    :param device: 可操作设备的实例
    :param screen_obj: 待测场景的实例
    :return:
    """
    num = 0
    while True:
        if num == 5:
            print("[-] Fake Screen!")
            return False
        print("[+] goback to ", screen_obj.vector)
        startcommand = screen_obj.command[0]
        cmd = startcommand
        result = subprocess.check_output(cmd, shell=True)
        # 检查是否正确进入我们设定的Activity内
        tmp = 0
        flag = False
        while True:
            if tmp == 10:
                return False
            time.sleep(0.3)
            cmd = "adb " + " -s " + device.dev_id + " shell dumpsys activity activities " + " | grep mResumedActivity"
            result = subprocess.check_output(cmd, shell=True)
            check_name = screen_obj.start
            if check_name in result.decode("utf8"):
                print("[+] start Act !")
                flag = True
                break
            tmp = tmp + 1

        if not flag:
            return False

        # 进入Screen
        if screen_obj.widget_command != []:
            try:
                flag = True
                for widget in screen_obj.widget_command:
                    try:
                        time.sleep(0.3)
                        print(widget.info)
                        widget.click()
                        time.sleep(0.3)
                    except:
                        print("[+] Don't widget_command : ")
                        print(widget.info)
                        flag = False
                        break
                if flag:
                    print("[+] start widget_command !")
                    return True
                else:
                    num = num + 1
                    pass
            except:
                num = num + 1
                pass
        else:
            return True


def monkey_test(project, device, screen_obj):
    fuzz_num = 10  # 默认每个场景测试1000轮
    with open(project.fuzzlog, 'w') as f:
        f.writelines("FUZZ-LOG")
    for rou in range(fuzz_num):
        # 进入到默认Screen
        flag = goback(device, screen_obj)
        if flag:
            print("[+] Success go back！")
        else:
            print("[-] Don't Success go back！")
            break
        cmd = "adb shell monkey "
        cmd = cmd + "-v" + " 5"
        cmd = cmd + " -s " + device.dev_id
        result = subprocess.check_output(cmd, shell=True)
        with open(project.fuzzlog, 'a') as f:
            f.writelines(result.decode('utf8'))


def init(addsc, project, device):
    """
    :param addsc: 新增加的场景列表
    :param project: 新项目的类实例
    :param device: 可操作设备的类实例
    :return:
    """
    # install apk
    apk_path = project.apk_path
    cmd = "adb -s " + device.dev_id + " install " + apk_path
    cmd = cmd + " -s " + device.dev_id
    result = subprocess.check_output(cmd, shell=True)
    if b"Success" in result:
        print("[+] Success install apk: ", apk_path)
    # 初始化场景对象列表
    sc_obj = []
    for newsc in addsc:
        for obj in project.screenobject:
            if obj.vector == newsc:
                sc_obj.append(obj)
                break
    # 开始模糊测试
    for obj in sc_obj:
        monkey_test(project, device, obj)
    # 卸载并清理环境
    device.uiauto.app_clear(project.used_name)
    cmd = "adb uninstall " + project.used_name
    cmd = cmd + " -s " + device.dev_id
    result = subprocess.check_output(cmd, shell=True)
    time.sleep(0.5)
    if "Success" in result.decode("utf8"):
        print("[+] Success uninstall :", project.p_id)
    else:
        print("[-] Don't uninstall :", project.p_id)
