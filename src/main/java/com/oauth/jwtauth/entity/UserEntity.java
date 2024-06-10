package com.oauth.jwtauth.entity;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import com.oauth.jwtauth.entity.enumentity.UserStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
@Entity
@Table(name = "Users" ,indexes  = {@Index(name="idx_email",columnList = "email")})
@Data
public class UserEntity implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull(message = "Username should not be null")
    @NotEmpty(message = "Username is required")
    @Column(unique = true)
    private String username;
    @NotEmpty(message = "Password required")
    @NotNull(message = "Password should not be null")
    private String password;
    @Email(message = "Email required")
    @NotNull(message = "Email should not be null")
    @NotEmpty(message = "Email is required")
    @Column(unique = true)
    private String email;
    private String avatar;
    @NotEmpty(message = " Role is required")
    @NotNull(message = "Role should not be null")
    private String role;
    private String refreshToken;
    private Long refreshTokenExpiry;
    private String otp;
    private long otpExpires;
    private UserStatus status;
    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role));
    }
    @Override
   public String getPassword(){
    return password;
   };
   @Override
   public String getUsername(){
    return username;
   }
   public UserEntity(){
    this.status = UserStatus.PENDING;
    this.role = "USER";
    this.createdAt = LocalDateTime.now();
    this.updatedAt = LocalDateTime.now();
   }
   
}










    // @OneToMany(mappedBy = "seller" , cascade = CascadeType.ALL , orphanRemoval = true)
    // @JsonManagedReference
    // private List<Products> products;
    // @JsonManagedReference
    // @OneToMany(mappedBy = "user", cascade = CascadeType.ALL , orphanRemoval = true)
    // private List<Order> orders;
    // @OneToMany(mappedBy = "user" , cascade = CascadeType.ALL , orphanRemoval = true)
    // @JsonManagedReference
    // private List<Address> address;
    // @OneToMany(mappedBy = "seller" , cascade = CascadeType.ALL , orphanRemoval = true)
    // @JsonManagedReference
    // private List<CouponCode> couponCodes;