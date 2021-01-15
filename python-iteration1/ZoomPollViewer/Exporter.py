#!/usr/bin/env python3
"""

ZOOM POLL VIEWER v0.1

"""
import xlsxwriter
from datetime import date


class Exporter():
    def __init__(self,zpv):
        self.zpv = zpv
        self.path = "./"
    # To Create xlsx File
    def create_xlsx(self,name:str):
        self.xlsx_file = xlsxwriter.Workbook(self.path+name)
    # To Create a Page in xlsx File
    def create_xlsx_page(self,name:str):
        self.xlsxpage = self.xlsx_file.add_worksheet(name) 
    # Writing Data
    def write_xlsx_page_data(self,col:int,row:int,data:str):
        self.xlsxpage.write(col,row,data)
    def write_xlsx_page_data_title(self,col:int,row:int,data:str):
        self.xls_cell_format = self.xlsx_file.add_format({'bold':True})
        self.xlsxpage.write(col,row,data,self.xls_cell_format)
    def write_xlsx_page_chart(self,col,row,data):
        self.chart = self.xlsx_file.add_chart()
    def close_xlsx(self):
        self.xlsx_file.close()        
    def export_global(self):
        now = date.today()
        r = 1
        self.create_xlsx('Global.xlsx')
        self.create_xlsx_page(str(now))
        self.write_xlsx_page_data_title(0,0,'BYS')        
        self.write_xlsx_page_data_title(1,0,'ID')
        self.write_xlsx_page_data_title(1,1,'NAME')
        self.write_xlsx_page_data_title(1,2,'SURNAME')
        self.write_xlsx_page_data_title(0,4,'POLL NAME')
        self.write_xlsx_page_data_title(1,3,'DATE')
        self.write_xlsx_page_data_title(1,4,'NOQ')
        self.write_xlsx_page_data_title(1,5,'SP')
        for student in self.zpv._students:            
            if student._email != None:
                c = 0
                r += 1
                self.write_xlsx_page_data(r,c,str(student._student_id)) 
                c += 1
                self.write_xlsx_page_data(r,c,str(student._first_name+" "+student._middle_name)) 
                c += 1
                self.write_xlsx_page_data(r,c,student._last_name)
                for response in student._responses:
                    if response._poll._type == 'QUIZ':
                        c += 1
                        date = date.strftime(response._session._date_text)
                        self.write_xlsx_page_data(r,c,date) 
                        c += 1
                        self.write_xlsx_page_data(r,c,response._poll._number_of_questions) 
                        c += 1
                        self.write_xlsx_page_data(r,c,"Static")    
        self.close_xlsx()                

#m = Exporter()
# m.create_xlsx('Poll.xlsx')
# m.create_xlsx_page('Pager-1')
# m.write_xlsx_page(0,0,'Selam')
# m.create_xlsx_page('Pager-2')
# m.write_xlsx_page(0,0,'Selam2')
#m.close_xlsx()