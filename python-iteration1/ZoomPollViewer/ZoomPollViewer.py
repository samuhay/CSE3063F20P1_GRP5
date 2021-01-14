#!/usr/bin/env python3
"""

ZOOM POLL VIEWER v0.1

"""
from datetime import datetime
import logging
from .Student import Student
from .Poll import Poll
from .Session import Session
from .GUI import GUI
from .Importer import Importer
from .Logger import Logger


class ZoomPollViewer:

    def __init__(self):

        self._students = []
        self._sessions = []
        self._polls = []

        self.GUI = GUI(self)
        self.importer = Importer(self)

        self.date_time_format = "%b %d, %Y %H:%M:%S"
        self.file_name_date_format = "%Y%m%d"
        self._is_logger_active = True
        self._import_completed = False

        self.GUI.root.mainloop()

    def get_student(self, full_name, email):
        found = False
        for student in self._students:
            if student._email == email:
                found = student
        if not found:
            full_name = str(full_name).split(" ")
            first_name = full_name[0]
            last_name = full_name[-1]
            for student in self._students:
                if str(student._first_name).lower() == first_name.lower() and str(student._last_name).lower() == last_name.lower():
                    student._email = email
                    return student
        return found

    def get_poll_by_question(self, question_text):
        for poll in self._polls:
            for question in poll.get_questions():
                if question_text == question.get_text():
                    return poll
        return False

    def get_poll_by_name(self, poll_name, poll_type):
        for poll in self._polls:
            if poll.get_name() == poll_name:
                return poll
        return False

    def get_session(self, date_time_text):
        date_time_object = datetime.strptime(date_time_text, self.date_time_format)
        date_text = date_time_object.strftime(self.file_name_date_format)
        for session in self._sessions:
            if session.get_date_text() == date_text:
                return session
        return False
        #Logger(ZoomPollViewer.add_session.__name__,"deneme")

    def add_student(self, firstname, surname, student_id):
        student = Student(self, firstname, surname, student_id)
        self._students.append(student)
        # @todo It may check if user exist
        return student

    def add_session(self, date_time_text):
        session = self.get_session(date_time_text)
        if session:
            return session
        else:
            date_time_object = datetime.strptime(date_time_text, self.date_time_format)
            session = Session(self, date_time_object)
            self._sessions.append(session)
            return session

    def add_poll(self, poll_name, poll_type="QUIZ"):
        for poll in self._polls:
            if poll.get_name() == poll_name:
                return poll
        poll = Poll(self, poll_name, poll_type)
        self._polls.append(poll)
        return poll

    def check_student_email(self, email):
        for student in self._students:
            if student.get_email() == email:
                return True
        return False

    def metrics_calculator(self):
        for session in self._sessions:
            session_attendance = 0
            for poll in session.get_polls():
                poll_grades = []
                poll_attendance = 0
                for student in self._students:
                    response = student.get_response_by_session_and_poll(session, poll)
                    if response:
                        if poll.get_type() == "ATTENDANCE":
                            student.add_session_attendance(session)
                            session_attendance += 1
                            poll_attendance += 1
                        elif poll.get_type() == "QUIZ":
                            poll_grades.append(response.get_grade())
                            poll_attendance += 1
                poll.set_session_grades(session, poll_grades)
                poll.set_session_number_of_students(session, poll_attendance)
                print(poll, poll_attendance)
            session.set_attendance(session_attendance)
        self.student_metrics_calculator()

    def student_metrics_calculator(self):
        for student in self._students:
            student.calculate_grades()

    def session_metrics_calculator(self):
        pass

    def poll_metrics_calculator(self):
        pass

