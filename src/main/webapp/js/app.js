angular.module('attendanceApp', [])
    .controller('MainController', ['$http', function ($http) {
        var self = this;
        self.view = 'mark';
        var today = new Date();
        var year = today.getFullYear();
        var month = String(today.getMonth() + 1).padStart(2, '0');
        var day = String(today.getDate()).padStart(2, '0');
        self.attendanceDate = `${year}-${month}-${day}`;
        self.recordDate = '';
        self.students = [];
        self.records = [];
        self.alert = { message: '', type: '' };
        self.isSubmitting = false;

        self.formatDate = function (dateValue) {
            if (!dateValue) {
                return '';
            }
            // If it's already a string, extract just the date part
            if (typeof dateValue === 'string') {
                return dateValue.split('T')[0];
            }
            // If it's a Date object, use local timezone conversion (not UTC)
            if (dateValue instanceof Date && !isNaN(dateValue.getTime())) {
                var year = dateValue.getFullYear();
                var month = String(dateValue.getMonth() + 1).padStart(2, '0');
                var day = String(dateValue.getDate()).padStart(2, '0');
                return `${year}-${month}-${day}`;
            }
            return dateValue;
        };

        self.switchView = function (view) {
            self.view = view;
            if (view === 'view') {
                self.loadAttendance();
            }
        };

        self.loadStudents = function () {
            console.log('Loading students...');
            $http.get('/attendance-management-system-1.0.0/students')
                .then(function (response) {
                    console.log('Students loaded:', response.data);
                    self.students = response.data.map(function (student) {
                        student.status = 'Absent';
                        return student;
                    });
                })
                .catch(function (error) {
                    console.error('Error loading students:', error);
                    self.showAlert('Unable to load students. Please check the backend connection.', 'error');
                });
        };

        self.setStatus = function (student, status) {
            student.status = status;
        };

        self.markAllPresent = function () {
            self.students.forEach(function (student) {
                student.status = 'Present';
            });
            self.showAlert('All students marked as Present!', 'success');
        };

        self.markedCount = function () {
            return self.students.filter(function (student) {
                return student.status === 'Present';
            }).length;
        };

        self.submitAttendance = function () {
            var formattedDate = self.formatDate(self.attendanceDate);
            if (!formattedDate) {
                self.showAlert('Select a valid date before submitting attendance.', 'error');
                return;
            }

            self.isSubmitting = true;
            var payload = {
                date: formattedDate,
                records: self.students.map(function (student) {
                    return {
                        studentId: student.id,
                        status: student.status
                    };
                })
            };

            console.log('Submitting attendance:', payload);
            $http.post('/attendance-management-system-1.0.0/attendance', payload)
                .then(function (response) {
                    self.showAlert(response.data.message || 'Attendance submitted successfully.', 'success');
                    self.isSubmitting = false;
                    self.recordDate = '';
                    self.loadAttendance();
                })
                .catch(function () {
                    self.showAlert('Failed to submit attendance. Please try again.', 'error');
                    self.isSubmitting = false;
                });
        };

        self.loadAttendance = function () {
            var params = {};
            if (self.recordDate) {
                // Ensure recordDate is converted to YYYY-MM-DD string format (using local timezone)
                var dateStr = self.formatDate(self.recordDate);
                if (dateStr) {
                    params.date = dateStr;
                }
            }

            console.log('Loading attendance with params:', params);
            $http.get('/attendance-management-system-1.0.0/view-attendance', { params: params })
                .then(function (response) {
                    console.log('Attendance records loaded:', response.data);
                    self.records = response.data;
                })
                .catch(function (error) {
                    console.error('Error loading attendance:', error);
                    self.showAlert('Unable to fetch attendance records.', 'error');
                });
        };

        self.clearFilter = function () {
            self.recordDate = '';
            self.loadAttendance();
        };

        self.showAlert = function (message, type) {
            self.alert.message = message;
            self.alert.type = type;
            setTimeout(function () {
                self.alert.message = '';
                self.alert.type = '';
            }, 5000);
        };

        self.loadStudents();
        self.loadAttendance();
    }]);
