import os
import subprocess
import time

def shot(devices, project, name):
    """
    :param devices: uiautomator2操作对象
    :param project: 这轮APK的项目对象
    :param name: 将要保存的图片名字
    :return:
    """
    pc_dir = os.path.join(project.screenshot_dir, name + ".png")
    if not os.path.exists(project.screenshot_dir):
        os.mkdir(project.screenshot_dir)
    devices.screenshot(pc_dir)
    # 检查PC中是否已经存在该文件
    while True:
        flag = 0
        for file in os.listdir(project.screenshot_dir):
            if name in file:
                flag = 1
        if flag == 1:
            print("[+] PC get shoot: ", name + ".png")
            break
    return pc_dir

