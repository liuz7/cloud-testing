package com.vmware.vchs.model.portal.iamRole.rolepermission;

/**
 * Created by fanz on 5/14/15.
 */
public class UserBase {
        private String name;
        private String firstName;
        private String lastName;
        private String email;

        public String getName() {
            return name;
        }

        public String getFirstName() {
            return firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public String getEmail() {
            return email;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public void setEmail(String email) {
            this.email = email;
        }

}
