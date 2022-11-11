from devices_list import remote_device, local_device


def scan_devices(flag):
    devices_list = []
    if flag == 0:
        devices_list = remote_device.remote_connect()
    elif flag == 1:
        devices_list = local_device.scan_devices()
    if not devices_list:
        print("[-] false scanf devices!")
    return devices_list
