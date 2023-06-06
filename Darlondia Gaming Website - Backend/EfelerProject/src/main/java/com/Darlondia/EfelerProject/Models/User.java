    package com.Darlondia.EfelerProject.Models;

    import org.springframework.data.annotation.Id;
    import org.springframework.data.mongodb.core.mapping.Document;
    import org.springframework.data.mongodb.core.mapping.Field;

    import java.time.LocalDateTime;
    import java.util.ArrayList;
    import java.util.List;


    import java.time.LocalDateTime;
    @Document(collection = "users")
    public class User {

        @Id
        private String id;

        @Field("username")
        private String username;

        @Field("email")
        private String email;

        @Field("password")
        private String password;

        @Field("account_creation_date")
        private LocalDateTime accountCreationDate;

        @Field("profile_picture")
        private String profilePicture;

        @Field("bio")
        private String bio;

        @Field("terms_accepted")
        private Boolean termsAccepted;

        @Field("role")
        private String role = "USER"; // set a default value of "USER"

        @Field("two_factor_auth_enabled")
        private Boolean twoFactorAuthEnabled;

        private transient String passwordConfirmation; // added field for password confirmation

        @Field("followers")
        private List<String> followers = new ArrayList<>();

        @Field("following")
        private List<String> following = new ArrayList<>();
        @Field("reset_code")
        private String resetCode;

        @Field("reset_code_expiration")
        private LocalDateTime resetCodeExpiration;
        @Field("game_lists")
        private List<String> gameLists = new ArrayList<>();

        // Add getters and setters for each field here

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public LocalDateTime getAccountCreationDate() {
            return accountCreationDate;
        }

        public void setAccountCreationDate(LocalDateTime accountCreationDate) {
            this.accountCreationDate = accountCreationDate;
        }

        public String getProfilePicture() {
            return profilePicture;
        }

        public void setProfilePicture(String profilePicture) {
            this.profilePicture = profilePicture;
        }

        public String getBio() {
            return bio;
        }

        public void setBio(String bio) {
            this.bio = bio;
        }

        public Boolean getTermsAccepted() {
            return termsAccepted;
        }

        public void setTermsAccepted(Boolean termsAccepted) {
            this.termsAccepted = termsAccepted;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public Boolean getTwoFactorAuthEnabled() {
            return twoFactorAuthEnabled;
        }

        public void setTwoFactorAuthEnabled(Boolean twoFactorAuthEnabled) {
            this.twoFactorAuthEnabled = twoFactorAuthEnabled;
        }

        public String getPasswordConfirmation() {
            return passwordConfirmation;
        }

        public void setPasswordConfirmation(String passwordConfirmation) {
            this.passwordConfirmation = passwordConfirmation;
        }
        public List<String> getFollowers() {
            return followers;
        }

        public void setFollowers(List<String> followers) {
            this.followers = followers;
        }

        public List<String> getFollowing() {
            return following;
        }

        public void setFollowing(List<String> following) {
            this.following = following;
        }
        public String getResetCode() {
            return resetCode;
        }

        public void setResetCode(String resetCode) {
            this.resetCode = resetCode;
        }

        public LocalDateTime getResetCodeExpiration() {
            return resetCodeExpiration;
        }

        public void setResetCodeExpiration(LocalDateTime resetCodeExpiration) {
            this.resetCodeExpiration = resetCodeExpiration;
        }
        public List<String> getGameLists() {
            return gameLists;
        }

        public void setGameLists(List<String> gameLists) {
            this.gameLists = gameLists;
        }

    }

