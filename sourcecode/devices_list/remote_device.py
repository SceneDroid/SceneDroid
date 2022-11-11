# -*- coding: utf-8 -*-
import subprocess
import uiautomator2 as u2
from structure import phone
phone_list = []
remote_list = ["x.x.x.x"]


def remote_connect():
    global remote_list, succe_list
    if remote_list:
        print("[!] start connect remote phone!")
        for ip in remote_list:
            print("[#] Try connect: ", ip)
            device_ip = ip + ":5555"
            d = u2.connect_adb_wifi(device_ip)
            device_info = d.info
            if device_info != {}:
                print("[+] connected to: ", ip)
                newphone = phone.usephone(d, device_ip)
                phone_list.append(newphone)
            else:
                print("[-] false to connect: ", ip)
            # 注册监听器
            d.watcher("允许").when(xpath="拒绝").when("允许").click()
            d.watcher.when("允许").click()
            # 开始后台监控
            d.watcher.start()
            # 使用ui2的输入法取代系统输入法
            d.set_fastinput_ime(True)

    cmd = "python3 -m uiautomator2 init"
    result = subprocess.check_output(cmd, shell=True)
    if "Successfully init" in result.decode('utf-8'):
        print("[+] Successfully init atx-agent!")
    else:
        print("[-] Fault init atx-agent!")
        exit(0)
    return phone_list

if __name__ == '__main__':
    result = remote_connect()
    print(result)
