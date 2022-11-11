import subprocess
import time

def init(project, device, entry):
    print("[+] goback to ", entry.vector)
    startcommand = entry.startadb
    cmd = startcommand
    print("[CMD]", cmd)
    result = subprocess.check_output(cmd, shell=True)
    # 检查是否正确进入我们设定的Activity内
    num = 0
    flag = False
    while True:
        if num == 5:
            flag = True
            break
        try:
            time.sleep(0.5)
            cmd = "adb " + " -s " + device.dev_id + " shell dumpsys activity activities " + " | grep mResumedActivity"
            result = subprocess.check_output(cmd, shell=True)
            check_name = entry.activity
            if check_name in result.decode("utf8"):
                print("[+] start Act !")
                break
        except:
            pass
        num = num + 1

    if flag:
        return

    time.sleep(1)
    #print("HCHCHCHCHCHCHC")
    # 进入Screen
    #print(entry.widgets)
    #print("HBHBHBHBHBHBHB")
    if entry.widgets:
        entry.putself()
        for widget in entry.widgets:
            #print("HAHAHAHAHA")
            # Find Target Widget
            all_widgets = device.uiauto()
            #print(all_widgets)
            conwidget = ""
            flag = True
            for all_widget in all_widgets:
                print(all_widget.info)
                if all_widget.info["bounds"] == widget:
                    conwidget = all_widget
                    print("Find Widget : ", conwidget.info)
                    flag = False
                    break
            if flag:
                return
            time.sleep(0.3)
            print(conwidget.info)
            conwidget.click()
            time.sleep(0.3)

    with open(project.fuzzlog, 'w') as f:
        f.writelines("FUZZ-LOG")

    cmd = "adb -s " + device.dev_id + " shell monkey "
    cmd = cmd + " -p " + project.used_name
    cmd = cmd + " -v " + " 1000 "
    print("[CMD] ", cmd)
    result = subprocess.check_output(cmd, shell=True)
    with open(project.fuzzlog, 'a') as f:
        f.writelines(result.decode('utf8'))
    #device.uiauto.app_stop(project.used_name)
    #device.uiauto.app_clear(project.used_name)
