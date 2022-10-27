# proofHelper-v2
A proof assistant for the proof system in the textbook "Understanding Symbolic Logic Fifth Edition"

# Example (exercise from the book)
If you enter the following lines one by one: 
```
pr ~((Ex)~Fx&(Ex)(Gx&~Hx))
pr (x)Hx>~(Ex)(Zx&Wx)
pr ~(x)(Wx>Fx)
aspcp ~(Ex)~Zx
qn 4 (x)Zx
cqn 3 (Ex)(Wx&~Fx)
ei 6 Wa&~Fa
simp 7 Wa
simp 7 ~Fa
ui 5 Za
conj 10 8 Za&Wa
eg 11 (Ex)(Zx&Wx)
dn 12 ~~(Ex)(Zx&Wx)
mt 2 13
dem 1
eg 9 (Ex)~Fx
dn 16 ~~(Ex)~Fx
ds 15 17
cqn 18 (x)(Gx>Hx)
qn 14 (Ex)~Hx
ei 20 ~Hb
ui 19 Gb>Hb
mt 21 22
eg 23 (Ex)~Gx
qn 24 ~(x)Gx
cp
```

Then you will have built the following proof:
```
 1. ~((∃x)~Fx·(∃x)(Gx·~Hx))   Pr.
 2. (x)Hx⊃~(∃x)(Zx·Wx)        Pr.
 3. ~(x)(Wx⊃Fx)               Pr.
 4. ┌ ~(∃x)~Zx                Assp. (C.P.)
 5. │ (x)Zx                   Q.N.4
 6. │ (∃x)(Wx·~Fx)            C.Q.N.3
 7. │ Wa·~Fa                  E.I.6 (flag a)
 8. │ Wa                      Simp.7
 9. │ ~Fa                     Simp.7
10. │ Za                      U.I.5
11. │ Za·Wa                   Conj.10,8
12. │ (∃x)(Zx·Wx)             E.G.11
13. │ ~~(∃x)(Zx·Wx)           D.N.12
14. │ ~(x)Hx                  M.T.2,13
15. │ ~(∃x)~Fx∨~(∃x)(Gx·~Hx)  DeM.1
16. │ (∃x)~Fx                 E.G.9
17. │ ~~(∃x)~Fx               D.N.16
18. │ ~(∃x)(Gx·~Hx)           D.S.15,17
19. │ (x)(Gx⊃Hx)              C.Q.N.18
20. │ (∃x)~Hx                 Q.N.14
21. │ ~Hb                     E.I.20 (flag b)
22. │ Gb⊃Hb                   U.I.19
23. │ ~Gb                     M.T.21,22
24. │ (∃x)~Gx                 E.G.23
25. │ ~(x)Gx                  Q.N.24
    └─────                    
26. ~(∃x)~Zx⊃~(x)Gx           C.P.4-25
```
