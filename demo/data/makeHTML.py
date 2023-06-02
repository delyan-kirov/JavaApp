import os
import markdown

def readMD(mdFile):
    with open(mdFile, 'r') as f:
        mdata = f.read()
    return mdata

def buildMD(mdata, name):
    title = os.path.basename(name).replace('.html', '')
    cssStyle = f"<!DOCTYPE html> \n <head> \n <title>{title}</title> \n \
     <link rel=\"stylesheet\" href=\"/data/style.css\"> \n </head> \n"
    with open(name, 'w') as f:
        f.write(cssStyle)
        f.write(mdata)

def convert_markdown_files(directory):
    for root, dirs, files in os.walk(directory):
        for file in files:
            if file.endswith(".md"):
                mdFile = os.path.join(root, file)
                name = os.path.splitext(mdFile)[0] + ".html"
                mdata = readMD(mdFile)
                html = markdown.markdown(mdata, extensions=['markdown.extensions.tables'])
                buildMD(html, name)

convert_markdown_files(os.getcwd())