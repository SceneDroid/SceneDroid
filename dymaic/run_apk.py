import subprocess
import time

from dymaic import extra, currFrag, target, startact
from structure import mywidget, screen
from tools import findres, eigenvector, getshot

scess_start_activity = []
fault_start_activity = []


def start(project, device, other_s, activity, component, dcommnd):
    global scess_start_activity
    print("============  NEW START ACTIVITY ============")
    print("[START ACTIVITY]: ", activity)
    s = other_s
    action = s[0]
    category = s[1]
    print("[component]: ", component)
    if action != '':
        print("[action]: ", action)
    if category != '':
        print("[category]: ", category)
    myextras = []
    try:
        myextras = extra.get_extra_paras(project, activity)
    except:
        myextras = []
    if myextras != [] and not None:
        print("[+] GET EXTRAS: ", myextras)
    else:
        print("[-] DON'T GET EXTRAS")
    cmd = "timeout 20s adb -s " + device.dev_id + " shell am start -S -n " + component
    if not action == '':
        cmd = cmd + ' -a ' + action
    if not category == '':
        cmd = cmd + ' -c ' + category
    # 补充参数
    if myextras != [] and not None:
        for ex in myextras:
            cmd = cmd + ' ' + ex
    cmd = cmd + ' -W'
    print("[cmd]: ", cmd)
    with open(project.startActCmd, "a") as f:
        f.writelines(cmd + "\n")
    result = subprocess.check_output(cmd, shell=True)
    with open(project.startActCmdRes, "a") as f:
        f.writelines(result.decode('utf8') + "\n")
    if b"Status: ok" in result:
        dcommnd.append(cmd)
        print("[cmd]: ", cmd)
        time.sleep(0.5)
        short_act = activity.split(project.used_name)[1]
        print("[short_act]: ", short_act)
        if short_act in result.decode("utf8"):
            print("[+] short act in Run result!")
            if activity not in project.actcoverage:
                print("[+] successful append new coverage activity: ", activity)
                print("[+] Now act coverage :", project.actcoverage)
                if activity not in scess_start_activity:
                    scess_start_activity.append(activity)
                    project.actcoverage.add(activity)  # activity : com.example.mynav.SettingActivity
                    with open(project.successact, "a") as f:
                        f.writelines(activity + "\n")
    else:
        return "Fault"

    # 初始滑建立Screnn对象
    dxml = device.uiauto.dump_hierarchy(compressed=True)
    # 临时写入布局文件信息
    f = open(project.tmptxt, 'w')
    f.write(dxml)
    f.close()
    dtype = True
    dparentScreen = ""
    act = activity.split(project.used_name)[1]
    print("[act]: ", act)  # .MainActivity

    ''''
    # Find Target Widget
    all_widget = device.uiauto()
    target_widget = target.getarget(project, activity, all_widget)
    for widget in target_widget:
        new_widwget = mywidget.mywidget(widget)
        widget_stack.append(new_widwget)

    # 构建初始Widget Stack
    for widget in device.uiauto(clickable="true"):
        # print(widget.info)
        flag = True
        for twidget in widget_stack:
            if twidget.ui2.info['bounds'] == widget.info['bounds']:
                flag = False
                break
        if flag:
            new_widwget = mywidget.mywidget(widget)
            widget_stack.append(new_widwget)
        else:
            continue
        if widget.info['className'] == 'android.widget.EditText':
            # 检查输入文本框
            findres.find(project, widget.info, project.tmptxt)
    '''
    screenvector = ""
    # 生成特征向量
    try:
        screenvector = eigenvector.getVector(dxml, project)
        print("[screenvector] : ", screenvector)
    except:
        print("[-] Fault screenvector")
        print(dxml)
    # 临时截图
    try:
        device.uiauto.screenshot(project.tmppng)
        print("[+] Successfull get screenshot")
    except:
        pass

    if screenvector not in project.screenlist:
        print("[+] New Acr Start Screen")
        project.screenlist.add(screenvector)
    else:
        return "Exists"

    try:
        shot_dir = getshot.shot(device.uiauto, project, screenvector)
        print("[+] Get shot: ", shot_dir)
    except:
        pass

    # 建立新的场景对象
    print("Activity Screen")
    new_screen = screen.screen(vector=screenvector, sadb=cmd, act=activity, stype=True)
    # add screen object
    project.screenobject.append(new_screen)
    # add screen vector list
    project.screenlist.add(screenvector)

    with open(project.actScreen, "a") as f:
        f.writelines(activity + " : " + screenvector + "\n")

    currentFra = currFrag.getcurfrag(device, project)
    if currentFra.name != "":
        tmptrans = project.used_name + act + "->" + currentFra.name
        print("[NEW Trans] : ", tmptrans)
        if tmptrans not in project.inittrans:
            print("[REAL NEW Trans] : ", tmptrans)
            project.inittrans.add(tmptrans)
            new_screen.newfrag = True
            new_screen.stype = False
        new_screen.fragment = currentFra.name

    new_screen.printAll()
    startact.run(project, device, new_screen, currentFra)
    print("Success")
    return "Success"


def fault_start(fault_start_activity, project, device):
    father_obj = ""
    widget = ""
    for screen_obj in project.screenobject:
        print(screen_obj.nextact)
        for act in screen_obj.nextact:
            if act == fault_start_activity:
                father_obj = screen_obj
                break
    if father_obj == "":
        return False
    startact.restartScreen(project=project, source_screen=father_obj, device=device)

    with open(project.SecondStart, "a") as f:
        f.writelines(fault_start_activity + "\n")

    for w2act in father_obj.actrans:
        if w2act[0] == fault_start_activity:
            print("[+] Find True 2act widget : ", w2act[1].info)
            widget = w2act[1]

    try:
        widget.click()
        print("[+] widget click")
        # project.total_step = project.total_step + 1
    except:
        print("[-] widget don't click: ", widget.info)
        return False

    cmd = "adb " + " -s " + device.dev_id + " shell dumpsys activity activities " + " | grep mResumedActivity"
    result = subprocess.check_output(cmd, shell=True)
    # print(result.decode("utf8"))
    # 获取当前Activity的名称
    currentACT = result.decode("utf8").split(project.used_name + "/")[1].split(" ")[0]
    print("[CURRENT ACT]: ", currentACT)  # .MainActivity
    if project.used_name in currentACT:
        currentACT = ".activities" + currentACT.split(".activities")[1]
    coveract = project.used_name + currentACT
    if fault_start_activity != coveract:
        return False
    # 初始滑建立Screnn对象
    dxml = device.uiauto.dump_hierarchy(compressed=True)
    # 临时写入布局文件信息
    f = open(project.tmptxt, 'w')
    f.write(dxml)
    f.close()
    dtype = True
    dparentScreen = ""
    act = fault_start_activity.split(project.used_name)[1]
    print("[act]: ", act)  # .MainActivity

    # 生成特征向量
    screenvector = eigenvector.getVector(dxml, project)
    # 临时截图
    device.uiauto.screenshot(project.tmppng)

    if screenvector not in project.screenlist:
        print("[+] New Acr Start Screen")
        project.screenlist.add(screenvector)
    else:
        return "Exists"

    shot_dir = getshot.shot(device.uiauto, project, screenvector)
    print("[+] Get shot: ", shot_dir)

    # 建立新的场景对象
    print("Activity Screen")
    new_screen = screen.screen(vector=screenvector, sadb=cmd, act=fault_start_activity, stype=True)
    # add screen object
    project.screenobject.append(new_screen)
    # add screen vector list
    project.screenlist.add(screenvector)

    with open(project.actScreen, "a") as f:
        f.writelines(fault_start_activity + " : " + screenvector + "\n")

    currentFra = currFrag.getcurfrag(device, project)
    if currentFra.name != "":
        tmptrans = project.used_name + act + "->" + currentFra.name
        print("[NEW Trans] : ", tmptrans)
        if tmptrans not in project.inittrans:
            print("[REAL NEW Trans] : ", tmptrans)
            project.inittrans.add(tmptrans)
            new_screen.newfrag = True
            new_screen.stype = False
        new_screen.fragment = currentFra.name

    new_screen.printAll()
    startact.run(project, device, new_screen, currentFra)
    print("Success")
    return True



# 开启动态探索
def run(project, device):
    global scess_start_activity, fault_start_activity
    # install apk
    apk_path = project.apk_path
    cmd = "adb -s " + device.dev_id + " install " + apk_path
    try:
        result = subprocess.check_output(cmd, shell=True)
        if b"Success" in result:
            print("[+] Success install apk: ", apk_path)
        else:
            return
    except:
        return
    pairs = project.parseMain
    print("[pairs]", pairs)
    for activity, other in pairs.items():
        flag = "Fault"
        print("[OTHER]: ")
        print(other)
        # This is the defined format of uiautomator
        component = project.used_name + '/' + activity  # com.example.mynav/com.example.mynav.MainActivity
        dcommnd = []
        other.append(['', ''])
        for s in other:
            try:
                flag = start(project, device, s, activity, component, dcommnd)
                #if flag == "Exists":
                    #break
            except:
                continue
        if flag == "Fault":
            fault_start_activity.append(activity)
    print("[+] Successful start Activity: ", scess_start_activity)
    print("[-] Fault start Activity: ", fault_start_activity)

    # Try Start Fault start Activity
    for activity in fault_start_activity:
        try:
            res = fault_start(fault_start_activity=activity, project=project, device=device)
            if res:
                print("[+] New restart activity: ", activity)
            else:
                print("[-] Can't restart activity: ", activity)
        except:
            continue
