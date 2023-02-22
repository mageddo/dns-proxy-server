---
title: Multiple Environments
weight: 9
---

As **DPS 2.18** you can group your hostnames by environments, this is a very useful feature when you wanna solve
the same hostname to **different IPs** depending of what you're doing, e.g developing in your machine or testing the QA 
environment as the example below: 

Let's say you're developing at the **acme.com** so you wanna solve it to your local machine, then you can configure
DPS local entries as the following

![](https://i.imgur.com/tHwWeUT.png)

When you are done with your work and deployed to the QA environment and wanna test it, let's say your app is deployed at
the QA machine addressed by the IP `192.168.0.50`, so you can create a new env on **DPS** e.g. **QA** and create 
the `acme.com` **A** record pointing to the `192.168.0.50` address.

![](https://i.imgur.com/nAMCxcC.png)

This way you can swap between **local** and **qa** environments in a fast and convenient approach
