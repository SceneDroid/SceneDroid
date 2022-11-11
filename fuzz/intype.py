"""
https://developer.android.com/training/keyboard-input/style
//文本类型，多为大写、小写和数字符号。
android:inputType="none"//输入普通字符
android:inputType="text"//输入普通字符
android:inputType="textCapCharacters"//输入普通字符
android:inputType="textCapWords"//单词首字母大小
android:inputType="textCapSentences"//仅第一个字母大小
android:inputType="textAutoCorrect"//前两个自动完成
android:inputType="textAutoComplete"//前两个自动完成
android:inputType="textMultiLine"//多行输入
android:inputType="textImeMultiLine"//输入法多行（不一定支持）
android:inputType="textNoSuggestions"//不提示
android:inputType="textUri"//URI格式
android:inputType="textEmailAddress"//电子邮件地址格式
android:inputType="textEmailSubject"//邮件主题格式
android:inputType="textShortMessage"//短消息格式
android:inputType="textLongMessage"//长消息格式
android:inputType="textPersonName"//人名格式
android:inputType="textPostalAddress"//邮政格式
android:inputType="textPassword"//密码格式
android:inputType="textVisiblePassword"//密码可见格式
android:inputType="textWebEditText"//作为网页表单的文本格式
android:inputType="textFilter"//文本筛选格式
android:inputType="textPhonetic"//拼音输入格式
//数值类型
android:inputType="number"//数字格式
android:inputType="numberSigned"//有符号数字格式
android:inputType="numberDecimal"//可以带小数点的浮点格式
android:inputType="phone"//拨号键盘
android:inputType="datetime"//日期+时间格式
android:inputType="date"//日期键盘
android:inputType="time"//时间键盘
"""
import random


test_class = ["none", "text", "textCapCharacters", "textCapWords",
              "textCapSentences", "textAutoCorrect", "textAutoComplete",
              "textMultiLine", "textImeMultiLine", "textNoSuggestions",
              "textUri", "textEmailAddress", "textEmailSubject", "textShortMessage",
              "textLongMessage", "textPersonName", "textPostalAddress", "textPassword",
              "textVisiblePassword", "textWebEditText", "textFilter", "textPhonetic",
              "number", "numberSigned", "numberDecimal", "phone", "datetime", "date",
              "time"]
# 生成数值类型

def nonenum():
    lenstr = random.randint(8, 25)
    fuzz_str = ""
    for index in range(lenstr):
        while True:
            fuzz_ch = random.randint(0, 9)
            fuzz_str = fuzz_str + str(fuzz_ch)
            break
    print("[+] {nonenum} Success Create Fuzz Strings: ", fuzz_str)
    return fuzz_str


def signednum():
    lenstr = random.randint(8, 25)
    fuzz_str = ""
    for index in range(lenstr):
        while True:
            if index == 0:
                fuzz_ch = random.randint(-1, 1)
                if fuzz_ch == -1:
                    fuzz_str = fuzz_str + '-'
                else:
                    pass
                break
            else:
                fuzz_ch = random.randint(0, 9)
                fuzz_str = fuzz_str + str(fuzz_ch)
                break
    print("[+] {signednum} Success Create Fuzz Strings: ", fuzz_str)
    return fuzz_str


# 生成字符类型

def nonetype():
    lenstr = random.randint(8, 25)
    fuzz_str = ""
    for index in range(lenstr):
        while True:
            fuzz_ch = random.randint(48, 123)
            if 48 <= fuzz_ch <= 57 or 65 <= fuzz_ch <= 90 or 97 <= fuzz_ch <= 122:
                fuzz_str = fuzz_str + chr(fuzz_ch)
                break
    print("[+] {nonetype} Success Create Fuzz Strings: ", fuzz_str)
    return fuzz_str


def capwords():
    lenstr = random.randint(8, 25)
    fuzz_str = ""
    for index in range(lenstr):
        while True:
            if index == 0:
                fuzz_ch = random.randint(65, 91)
                fuzz_str = fuzz_str + chr(fuzz_ch)
                break
            else:
                fuzz_ch = random.randint(48, 123)
                if 48 <= fuzz_ch <= 57 or 65 <= fuzz_ch <= 90 or 97 <= fuzz_ch <= 122:
                    fuzz_str = fuzz_str + chr(fuzz_ch)
                    break
    print("[+] {capwords} Success Create Fuzz Strings: ", fuzz_str)
    return fuzz_str


def emailaddres():
    lenstr = random.randint(8, 20)
    fuzz_str = ""
    for index in range(lenstr):
        while True:
            if index == 0:
                fuzz_ch = random.randint(65, 91)
                fuzz_str = fuzz_str + chr(fuzz_ch)
                break
            else:
                fuzz_ch = random.randint(48, 123)
                if 48 <= fuzz_ch <= 57 or 65 <= fuzz_ch <= 90 or 97 <= fuzz_ch <= 122:
                    fuzz_str = fuzz_str + chr(fuzz_ch)
                    break
    fuzz_str = fuzz_str + '@'
    lenstr = random.randint(3, 6)
    for index in range(lenstr):
        while True:
            fuzz_ch = random.randint(48, 123)
            if 48 <= fuzz_ch <= 57 or 65 <= fuzz_ch <= 90 or 97 <= fuzz_ch <= 122:
                fuzz_str = fuzz_str + chr(fuzz_ch)
                break
    fuzz_str = fuzz_str + '.com'
    print("[+] {emailaddres} Success Create Fuzz Strings: ", fuzz_str)
    return fuzz_str


def create(inputType="none"):
    fuzz_str = ""
    if inputType == "none" and inputType == "text" and inputType == "text" and inputType == "textNoSuggestions" \
            and inputType == "textAutoCorrect" and inputType == "textAutoComplete" and inputType == "textMultiLine" \
            and inputType == "textImeMultiLine" and inputType == "textPassword" and inputType == "textVisiblePassword":
        fuzz_str = nonetype()
    elif inputType == "textCapWords":
        fuzz_str = capwords()
    elif inputType == "textEmailAddress" and inputType == 'textEmailSubject':
        fuzz_str = emailaddres()
    elif inputType == "number" and inputType == "phone":
        fuzz_str = nonenum()
    else:
        fuzz_str = nonetype()
    return fuzz_str


if __name__ == '__main__':
    while True:
        classse = random.randint(0, len(test_class)-1)
        create(test_class[classse])

