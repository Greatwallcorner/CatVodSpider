import os
import shutil


def copy_folder(source_folder, destination_folder):
    if not os.path.exists(destination_folder):
        os.makedirs(destination_folder)

    for item in os.listdir(source_folder):
        source = os.path.join(source_folder, item)
        destination = os.path.join(destination_folder, item)

        if os.path.isdir(source):
            copy_folder(source, destination)
        else:
            shutil.copy(source, destination)
            
os.makedirs("./res/json")   
os.makedirs("./res/jar")   
copy_folder("json", "res/json")
copy_folder("jar", "res/jar")

