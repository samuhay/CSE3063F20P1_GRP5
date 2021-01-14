#!/usr/bin/env python3
"""

ZOOM POLL VIEWER v0.1

"""
import csv, xlrd, os
from os import listdir
from os.path import isfile, join


class Importer():

    def __init__(self, zpv):

        self.zpv = zpv

    def import_bys(self, file_path):
        try:
            read_xls=xlrd.open_workbook(file_path)
            xl_sheet = read_xls.sheet_by_index(0)
            first_names = xl_sheet.col_values(4)
            last_names = xl_sheet.col_values(7)
            students_ids = xl_sheet.col_values(2)
            for i in range(min(len(first_names),len(last_names))):
                if first_names[i] == "" or first_names[i] == "Adı" or last_names[i] == "" or last_names[i] == "Soyadı":
                    pass
                else:
                    self.zpv.add_student(first_names[i], last_names[i], students_ids[i])
        except Exception as err:
            print("Error Occurred", err)

    def import_answer_key(self, file_path):
        # @todo CSV version not working well with semicolon delimeter
        print(file_path)
        paths = self.get_paths(file_path, ["xls", "csv"])
        print("PATHS: ", paths)
        for file_path in paths:
            if os.path.basename(file_path).endswith(".xls"):
                read_xls = xlrd.open_workbook(file_path)
                xl_sheet = read_xls.sheet_by_index(0)
                question_texts = xl_sheet.col_values(0)
                answer_texts = xl_sheet.col_values(1)
                poll_name = xl_sheet.cell(0,0).value
                if poll_name == "Attendance Poll":
                    poll = self.zpv.add_poll(poll_name, "ATTENDANCE")
                else:
                    poll = self.zpv.add_poll(poll_name)
                print(poll.get_name(), " added successfully.")
                for i in range(2, min(len(question_texts), len(answer_texts))):
                    question = poll.add_question(question_texts[i])
                    question.add_choice(answer_texts[i], 1)

            elif os.path.basename(file_path).endswith(".csv"):
                with open(file_path, newline='', encoding="utf8") as csv_file:
                    csv_array = csv.reader(csv_file)
                    i = 0
                    for row in csv_array:
                        if i == 0:
                            poll_name = row[0]
                            if poll_name == "Attendance Poll":
                                poll = self.zpv.add_poll(poll_name, "ATTENDANCE")
                            else:
                                poll = self.zpv.add_poll(poll_name)
                        else:
                            if len(row[0]) > 0:
                                question = poll.add_question(row[0])
                                question.add_choice(row[1], 1)
                        i += 1

    def import_poll_report(self, file_path):
        paths = self.get_paths(file_path, ["csv"])
        for file_path in paths:
            with open(file_path, newline='', encoding="utf8") as csv_file:
                csv_array = csv.reader(csv_file)
                poll_cache = {}
                i = 0
                for row in csv_array:
                    if i == 0:
                        pass
                    else:
                        if row[4] in poll_cache.keys():
                            poll = poll_cache[row[4]]
                        else:
                            for q_index in range(4, len(row), 2):
                                poll = self.zpv.get_poll_by_question(row[q_index])
                                if poll:
                                    break


                        session = self.zpv.add_session(row[3])
                        session.add_poll(poll)
                        full_name = row[1]
                        email = row[2]
                        student = self.zpv.get_student(full_name, email)
                        if student:
                            response = student.add_response(session, poll)
                            for j in range(4, len(row) - 1, 2):
                                response.add_answer(row[j], row[j + 1])
                        else:
                            pass
                    i = i + 1

    def get_paths(self, file_path, file_types):
        paths = []
        if os.path.isdir(file_path):
            temp_paths = [f for f in listdir(file_path) if isfile(join(file_path, f))]
            for temp_path in temp_paths:
                for file_type in file_types:
                    if os.path.basename(temp_path).endswith("."+file_type):
                        paths.append(file_path+"/"+temp_path)
        else:
            paths.append(file_path)
        return paths
