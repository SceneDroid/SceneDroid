from datetime import datetime

log_path = ""


def init(path):
    global log_path
    log_path = path
    with open(log_path, 'w') as f:
        pass
    strt = "[+] We will write mylog messages for here: " + log_path
    print(strt)
    wlog(strt)


def wlog(info):
    global log_path
    #print(log_path)
    with open(log_path, 'a+') as f:
        dt = datetime.now()
        str1 = dt.strftime('%Y-%m-%d %H:%M:%S %f')
        str2 = str1 + " " + info + "\n"
        #print("[log] ", str2)
        f.write(str2)
