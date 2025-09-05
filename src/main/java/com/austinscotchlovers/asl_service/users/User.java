package com.austinscotchlovers.asl_service.users;

import com.austinscotchlovers.asl_service.events.Event;
import com.austinscotchlovers.asl_service.users.security.Role;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "email", unique = true, nullable = false, length = 100)
    private String email;

    @Column(name = "username", unique = true, nullable = false, length = 50)
    private String username;

    @Column(name = "first_name", length = 50)
    private String firstName;

    @Column(name = "last_name", length = 50)
    private String lastName;

    @Column(name = "full_name", length = 100)
    private String name;

    @Column(name = "profile_picture_url")
    private String profilePictureUrl;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id", referencedColumnName = "id")
    private Address address;

    @ManyToMany
    @JoinTable(
            name = "user_attended_events",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "event_id")
    )
    private List<Event> attendedEvents;

    private User(UserBuilder builder) {
        this.email = builder.email;
        this.username = builder.username;
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.name = builder.name;
        this.profilePictureUrl = builder.profilePictureUrl;
        this.phoneNumber = builder.phoneNumber;
        this.role = builder.role;
        this.address = builder.address;
        this.attendedEvents = builder.attendedEvents;
    }

    public User() {
    }

    public static UserBuilder builder() {
        return new UserBuilder();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public List<Event> getAttendedEvents() {
        return attendedEvents;
    }

    public void setAttendedEvents(List<Event> attendedEvents) {
        this.attendedEvents = attendedEvents;
    }

    public static class UserBuilder {
        private String email;
        private String username;
        private String firstName;
        private String lastName;
        private String name;
        private String profilePictureUrl;
        private String phoneNumber;
        private Role role;
        private Address address;
        private List<Event> attendedEvents;

        public UserBuilder withEmail(String email) {
            this.email = email;
            return this;
        }

        public UserBuilder withUsername(String username) {
            this.username = username;
            return this;
        }

        public UserBuilder withFirstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public UserBuilder withLastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public UserBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public UserBuilder withProfilePictureUrl(String profilePictureUrl) {
            this.profilePictureUrl = profilePictureUrl;
            return this;
        }

        public UserBuilder withPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public UserBuilder withRole(Role role) {
            this.role = role;
            return this;
        }

        public UserBuilder withAddress(Address address) {
            this.address = address;
            return this;
        }

        public UserBuilder withAttendedEvents(List<Event> attendedEvents) {
            this.attendedEvents = attendedEvents;
            return this;
        }

        public User build() {
            return new User(this);
        }
    }
}
