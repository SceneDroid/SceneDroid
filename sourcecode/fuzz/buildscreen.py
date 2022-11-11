import os.path
import pickle
from structure import entry


def init(screen, project):
    screen.printAll()
    widget_bound = []
    if len(screen.widget_command) > 0:
        for widget in screen.widget_command:
            print(widget.info)
            widget_bound.append(widget.info)
    newentry = entry.myentry(activity=screen.act, vector=screen.vector, startadb=screen.command[0],
                             widgets=widget_bound)
    temp = os.path.join(project.storge, newentry.vector)
    newentry.putself()
    with open(temp, 'wb') as f:  # 打开文件
        pickle.dump(newentry, f)  # 用 dump 函数将 Python 对象转成二进制对象文件
