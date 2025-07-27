package com.example.BlogOnline.Model;


import jakarta.persistence.*;
import jdk.jfr.Enabled;
import lombok.Data;

@Entity
@Data
public class Posteo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    @ManyToOne(fetch = FetchType.EAGER)
private Usuario user;

    private boolean enabled= true;


}
