#!/usr/bin/env python3
"""

ZOOM POLL VIEWER v0.1

"""
from .Response import Response


class Student:

    def __init__(self, zpv, first_name, mid_name, last_name, student_id, temporary=False):

        self.zpv = zpv

        if isinstance(mid_name, list):
            self._middle_name = " ".join(mid_name)

        self._first_name = first_name
        self._middle_name = mid_name
        self._last_name = last_name
        self._email = None
        self._student_id = student_id
        self._temporary = temporary

        # @todo While creating object of this Student class, we need to check all students to ensure email is unique.
        # @todo Main class is going to has check_student_email method.

        self._responses = []
        self._attended_sessions = []
        self._grades = []

    def get_first_name(self):
        return self._first_name
    
    def get_middle_name(self):
        return self._middle_name

    def get_last_name(self):
        return self._last_name

    def get_name(self):
        if self._temporary:
            return self._first_name
        else:
            if len(self._middle_name) > 0:
                return str(self._first_name + " " + self._middle_name + " " + self._last_name)

            return self._first_name + " " + self._last_name

    def get_email(self):
        return self._email

    def get_student_id(self):
        return self._student_id

    def get_responses(self):
        return self._responses

    def get_temporary_state(self):
        return self._temporary

    def get_response_by_session_and_poll(self, session, poll):
        for response in self._responses:
            if response.get_session() == session and response.get_poll() == poll:
                return response
        return False

    def get_attendance(self):
        return len(self._attended_sessions)

    def get_absent(self):
        return self.zpv.get_number_of_sessions() - self.get_attendance()

    def get_attendance_percentage(self):
        number_of_sessions = self.zpv.get_number_of_sessions()
        if number_of_sessions > 0:
            return int(self.get_attendance() / number_of_sessions * 100)
        return 0

    def get_average_grade(self):
        if len(self._grades) > 0:
            return sum(self._grades) / len(self._grades)
        else:
            return 0

    def set_email(self, email):
        self._email = email

    def add_response(self, session, poll):
        response = Response(self, session, poll)
        self._responses.append(response)
        return response

    def add_session_attendance(self, session):
        if session not in self._attended_sessions:
            self._attended_sessions.append(session)

    def calculate_grades(self):
        for response in self._responses:
            if response.get_poll().get_type() == "QUIZ":
                self._grades.append(response.get_grade())


