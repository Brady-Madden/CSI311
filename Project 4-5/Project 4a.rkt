;Brady Madden
;Project 4a
;001333820
#lang racket
(define team  '(("Emp1" (57 57 80 47 68 56 84 65))
                ("Emp2" (57 69 57 84 87 71 77 69 61 48))
                ("Emp3" (46 47 61 65 81 64 40 77 51 78))
                ("Emp4" (70 68 89 41))
                ("Emp5" (45 48 74 83 40 44 70 85 98 86))
                ))
(define sum
  (lambda(lst)
     (if (null? lst)
         0
         (+ (car lst) (sum (cdr lst))))))
(define getEmpTotals
  (lambda(lst)
    (if(null? lst)
         '()
         (cons(list(car(car lst))(sum(car(cdr(car lst)))))(getEmpTotals (cdr lst))))))


(define getTeamTotals
  (lambda(lst)
    (if(null? lst)
       0
       (+ (sum(car(cdr(car lst)))) (getTeamTotals (cdr lst))))))

(getEmpTotals team)
(getTeamTotals team)
