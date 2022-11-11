# -*- coding: utf-8 -*-
import subprocess
import uiautomator2 as u2
from structure import phone

phone_list = []


def scan_devices():
    global phone_list
    cmd = "adb devices"
    result = subprocess.check_output(cmd, shell=True)
    if result != "":
        print(result)
        line = result.split(b'\n')
        for index in range(1, len(line) - 1):
            tmp = line[index].split(b'	')[0].decode('utf-8')
            print("[+] Local: ", tmp)
            if tmp != "":
                d = u2.connect(tmp)
                device_info = d.info
                if device_info != {}:
                    print("[+] connected to: ", tmp)
                    newphone = phone.usephone(d, tmp)
                    phone_list.append(newphone)
                else:
                    print("[-] false to connect: ", tmp)
                # 注册监听器
                d.watcher("允许").when(xpath="拒绝").when("允许").click()
                d.watcher.when("允许").click()
                d.watcher.when("YES").click()
                d.watcher.when("ALLOW").click()
                d.watcher.when("OK").click()
                # 开始后台监控
                d.watcher.start()
                # 使用ui2的输入法取代系统输入法
                d.set_fastinput_ime(True)
                # devices_list.append(tmp)

    cmd = "python3 -m uiautomator2 init"
    result = subprocess.check_output(cmd, shell=True)
    if "Successfully init" in result.decode('utf-8'):
        print("[+] Successfully init atx-agent!")
    else:
        print("[-] Fault init atx-agent!")
        exit(0)

    print(phone_list)
    return phone_list


def local_connect():
    pass


if __name__ == '__main__':
    scan_devices()
