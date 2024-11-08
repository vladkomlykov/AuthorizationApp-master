package com.example.authorizationapp;

import java.util.Random;

public class SalaryGeneration {

    public double engineerSalary(){
        return (int) (Math.random() * 100000 - 70000) + 70000;
    }

    public double managerSalary(){
        return (int) (Math.random() * 90000 - 50000) + 50000;
    }

    public double administratorSalary(){
        return (int) (Math.random() * 120000 - 60000) + 60000;
    }
}
