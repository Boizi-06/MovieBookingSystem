import zipfile
import xml.etree.ElementTree as ET
import os

docx_path = r"c:\Users\admin\OneDrive\Desktop\MovieBookingSystem\docx\SRS.docx"
output_path = r"c:\Users\admin\OneDrive\Desktop\MovieBookingSystem\docx\SRS.txt"

def docx_to_txt(docx_file, txt_file):
    try:
        with zipfile.ZipFile(docx_file) as z:
            xml_content = z.read('word/document.xml')
            
        root = ET.fromstring(xml_content)
        
        # Word XML namespace URLs
        ns = '{http://schemas.openxmlformats.org/wordprocessingml/2006/main}'
        
        paragraphs = []
        for paragraph in root.iter(ns + 'p'):
            texts = [node.text for node in paragraph.iter(ns + 't') if node.text]
            if texts:
                paragraphs.append("".join(texts))
            else:
                # Avoid adding excessive empty lines, only add if the last one was not empty
                if paragraphs and paragraphs[-1] != "":
                    paragraphs.append("")
                
        with open(txt_file, 'w', encoding='utf-8') as f:
            f.write("\n".join(paragraphs))
        print("Success: Extracted text to", txt_file)
    except Exception as e:
        import traceback
        traceback.print_exc()

if __name__ == '__main__':
    docx_to_txt(docx_path, output_path)
