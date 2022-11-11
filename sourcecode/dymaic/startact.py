import copy
import os
import subprocess
import time

from fuzz import intype
from structure import mywidget, screen
from dymaic import target, currFrag
from tools import findres, eigenvector, getshot


def restartScreen(project, source_screen, device):
    """
    :param device:
    :param project: 项目对象
    :param screen: 场景对象
    :return: True or False 表示是否成功
    """
    print("restartScreen")
    # 恢复初始场景
    num = 0
    while True:
        # screen.printAll()
        # 如果一直重启场景失败，选择强制关闭Activity
        if num == 3:
            device.uiauto.app_stop(project.used_name)
            return False
        time.sleep(0.5)
        cmd = source_screen.adb
        # print(cmd)
        result = subprocess.check_output(cmd, shell=True)
        # print(result)
        time.sleep(0.5)
        '''
        cmd = "adb " + " -s " + device.dev_id + " shell dumpsys activity activities " + " | grep mResumedActivity"
        result = subprocess.check_output(cmd, shell=True)'''
        texactivity = source_screen.act.split(project.used_name)[-1]
        # print("texactivity: ", texactivity)
        # print("result: ", result.decode("utf8"))
        # check_name = project.used_name + '/' + texactivity
        if texactivity in result.decode("utf8"):
            print("[+] start Act !")
            break
        else:
            print("[-] can't start: ", texactivity)
        num = num + 1
    if source_screen.rescommand:
        try:
            for widget in source_screen.rescommand:
                try:
                    time.sleep(0.5)
                    print(widget.info)
                    widget.click()
                    time.sleep(0.5)
                except:
                    print("[+] Don't widget_command : ")
                    print(widget.info)
                    continue
                    # exit(0)
        except:
            pass
        print("[+] start widget_command !")
    return True


def run(project, device, source_screen, fragment):
    """
    :param project: 项目对象
    :param device: 设备对象
    :param screen: 场景对象
    :return:
    """
    print("[+] run : ", source_screen.vector)
    widget_stack = []
    activity = source_screen.act
    print("[+] Screen ACT: ", activity)
    # 初始滑建立Screnn对象
    dxml = device.uiauto.dump_hierarchy(compressed=True)
    # 临时写入布局文件信息
    f = open(project.tmptxt, 'w')
    f.write(dxml)
    f.close()

    # 设置输入框文本
    # 动态Fuzz
    all_widget = device.uiauto()
    for widget in all_widget:
        widgetu2 = widget
        if widgetu2.info['className'] == 'android.widget.EditText':
            print("Find Input Widget: ", widgetu2.info)
            # 检查输入文本框
            res = findres.find(project, widgetu2.info, project.tmptxt)
            if res:
                inputType = res[0]
            else:
                inputType = 'none'
            fuzz_str = intype.create(inputType)
            print("[+] Screen fuzz_str: ", fuzz_str)
            try:
                # widgetu2.click()
                widgetu2.set_text(fuzz_str)
            except:
                continue


    # Find Target Widget
    print("Find Target Widget")
    try:
        target_widget = target.getarget(project, activity, all_widget)
        for widget in target_widget:
            widget_stack.append(widget)
    except:
        pass
    print("[Target Widget] : ", widget_stack)

    # 构建初始Widget Stack
    print("Build init Widget")
    for widget in device.uiauto(clickable="true"):
        try:
            flag = True
            for t_widget in widget_stack:
                if t_widget.info == widget.info:
                    flag = False
                    break
            if flag:
                widget_stack.append(widget)
            else:
                continue
        except:
            continue

    print("======== wdiget stack ========")
    for widget in widget_stack:
        print(widget.info)
    print("==============================")

    flag = False
    while len(widget_stack) != 0:
        print("[++++++++++++++++++]")
        restartScreen(project, source_screen, device)
        time.sleep(0.5)
        widget = ""
        # 组件丢失的情况
        try:
            widget = widget_stack.pop()
            print(widget.info)
        except:
            # 重启场景
            print("[-] This widget break")
            # 测下一个组件
            continue

        try:
            widget.click()
            print("[+] widget click")
            # project.total_step = project.total_step + 1
        except:
            print("[-] widget don't click: ", widget.info)
            continue

        time.sleep(0.5)
        # 判断是否会进入其它包名
        currentPackageName = device.uiauto.info['currentPackageName']
        if currentPackageName != project.used_name:
            # 发现进入新的PKG
            print("[+] jmup to another pkg: ", currentPackageName)
            # 将新的PKG转换关系添加
            pkgtrans = project.used_name + "->" + currentPackageName
            try:
                project.pkg_dog.node(project.used_name, project.used_name)
            except:
                pass
            try:
                project.pkg_dog.node(currentPackageName, currentPackageName)
                project.pkg_dog.edge(project.used_name, currentPackageName)
            except:
                pass
            if pkgtrans not in project.pkgtrans:
                project.pkgtrans.append(pkgtrans)
            flag = True
            device.uiauto.app_stop(currentPackageName)
            continue
        else:
            print("Alive Package")

        # 获取当前的Activity
        # 判断是否进入了与启动Activity不同的Activity
        # 这里上面已经判断包名，故这里的Activity一定是我们运行的APK包名
        cmd = "adb " + " -s " + device.dev_id + " shell dumpsys activity activities " + " | grep mResumedActivity"
        result = subprocess.check_output(cmd, shell=True)
        # print(result.decode("utf8"))
        # 获取当前Activity的名称
        currentACT = result.decode("utf8").split(project.used_name + "/")[1].split(" ")[0]

        print("[CURRENT ACT]: ", currentACT)  # .MainActivity

        if project.used_name in currentACT:
            currentACT = ".activities" + currentACT.split(".activities")[1]

        coveract = project.used_name + currentACT
        if coveract not in project.actcoverage:
            project.actcoverage.add(coveract)

        if source_screen.act != coveract:
            print("A Different Act Name: ", currentACT)
            # 将新的ATG转换关系添加
            print("[screen.act] : ", source_screen.act)
            print("[currentACT] : ", currentACT)
            # build new screen
            new_act = project.used_name + currentACT
            print("[NEW ACT] : ", new_act)
            # source_screen.actrans[new_act] = ""
            # print("step1")
            w2act = []
            w2act.append(new_act)
            w2act.append(widget)
            print(w2act)
            source_screen.actrans.append(w2act)
            # print("step2")
            print("[+] TEST actrans: ", source_screen.actrans)
            print(widget_stack)
            source_screen.nextact.append(new_act)
            with open(project.SecondStart, "a") as f:
                f.writelines(coveract + "\n")
            flag = True
            continue
        else:
            pass

        # parse fragment
        currentFra = ""
        realnewfrag = False
        # Is new Fragment?
        try:
            currentFra = currFrag.getcurfrag(device, project)
            print("[Current Fragment] : ", currentFra.name)
            if fragment.name != currentFra.name:
                realnewfrag = True
        except:
            pass

        # 判断当前是否出现了新的Screen
        dxml = device.uiauto.dump_hierarchy(compressed=True)
        # 临时写入布局文件信息
        f = open(project.tmptxt, 'w')
        f.write(dxml)
        f.close()
        # 生成特征向量
        screenvector = eigenvector.getVector(dxml, project)

        if screenvector not in project.screenlist:
            print("[+] find a new screen: ", screenvector)
            project.screenlist.add(screenvector)
            # 将新的Screen转换关系添加到项目中
            screentrans = source_screen.vector + "->" + screenvector
            xml_dir = os.path.join(project.layout_dir, screenvector + ".xml")
            # 写入布局文件信息
            f = open(xml_dir, 'w')
            f.write(dxml)
            f.close()
            # 判断是否新出现的场景转换关系
            if screentrans not in project.screentrans:
                project.screentrans.append(screentrans)
        else:
            print("[-] Screen is old: ", screenvector)
            # os.remove(project.tmppng)
            continue

        # 初始化ADB操作信息
        #sadb = source_screen.adb
        # 对新的Screen进行截图
        shot_dir = getshot.shot(device.uiauto, project, screenvector)
        print("[+] get shot: ", shot_dir)
        # 建立新的场景对象
        print("Activity Screen")
        rescommand = []
        print("screen.rescommand", source_screen.rescommand)
        for sourcewidget in source_screen.rescommand:
            rescommand.append(sourcewidget)
        # print("rescommand", rescommand)
        rescommand.append(widget)
        print("rescommand", rescommand)

        new_screen = screen.screen(vector=screenvector, sadb=source_screen.adb, act=source_screen.act)
        print("[+] Build New Screen Successful!")

        new_screen.rescommand = rescommand
        new_screen.printAll()
        project.screenobject.append(new_screen)
        # add new son screen
        source_screen.sonScreen.append(new_screen)

        if realnewfrag:
            new_screen.newfrag = True
            new_screen.fragment = currentFra.name

        # 进行递归深度探索
        if currentFra == "":
            currentFra = fragment
        try:
            run(project, device, new_screen, currentFra)
        except:
            print("======================================")
            print("[-] Run Fault!")
            print("======================================")
            restartScreen(project, source_screen, device)
            continue
        print("[-------------------]")
        restartScreen(project, source_screen, device)
        print("[+][widget] over a task: ", screenvector, "->", widget.info)
        print(screenvector, "-> widget stack: ", widget_stack)
        # continue
    print("[+][screen] over a task: ", source_screen.vector)
    return
