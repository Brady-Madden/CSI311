#lang racket

;Brady Madden
;Project 4b
;CSI 311

(define make-tswb 
  (lambda ()
    (let ((records '()))
      (lambda (command . args)
        (cond
          ((equal? command 'empty?)(null? records))
          ((equal? command 'add!)(set! records (cons (car args) records)))
          ((equal? command 'get)
           (letrec ((sort-records (lambda (r) (sort r (lambda (x y) (<= (car x) (car y)))))))
             (if (null? args)
                 (sort-records records)
                 (sort-records (filter (car args) records)))))
          ((equal? command 'analytic)
           (if (< (length args) 2)
               ((car args) records)
               ((car args) (filter (cadr args) records))))
          ((equal? command 'clear) (set! records '())))))))
          
(define do-avg
  (lambda (x)
    (/ (apply + x) (length x))))                  
(define average
  (lambda (x)
    (do-avg (map cadddr x))))

(define do-divide
  (lambda (x)
    (apply / x)))                  
(define divide
  (lambda (x)
    (do-divide (map cadddr x))))

(define do-sum
  (lambda (x)
         (apply + x)))
(define sum
(lambda(x)
        (do-sum (map cadddr x))))
                             
(define minimum
 (lambda (x)
    (do-min (map cadddr x))))
(define do-min
  (lambda (x)
    (car (sort x <=))))
                            
(define maximum
  (lambda (x)
    (do-max (map cadddr x))))
(define do-max
  (lambda (x)
    (car (sort x >=))))
                            
(define do-count
  (lambda (x)
    (length x)))
(define count
  (lambda (x)
    (do-count (map cadddr x))))
                            
(define median
  (lambda (x)
    (do-avg (map cadddr x))))
(define do-median
    (lambda (x)
    (apply * x)))

(define multiply
  (lambda (x)
    (do-mult (map cadddr x))))
(define do-mult
  (lambda (x)
    (apply * x)))

(define tswb (make-tswb))
(tswb 'add!     '(2 123 "temp1"  72.1))
(tswb 'add!     '(1 123 "temp1"  72.0)) 
(tswb 'add!     '(2 123 "press1" 29.9213))
(tswb 'add!     '(1 123 "press1" 29.9212))
(tswb 'add!     '(1 456 "temp1"  87.3))   
(tswb 'add!     '(1 456 "temp2"  87.4))   
(tswb 'add!     '(1 456 "press1" 28.9234))

(tswb 'empty?)
(tswb 'get)
(tswb 'get (lambda (l) (eqv? (cadr l) 456)))
(tswb 'get (lambda (l)(eqv? (caddr l) "temp1")))
(define (temp1-123 l)
(and (eqv? (cadr l) 123) (eqv? (caddr l) "temp1")))
(display "get: ")
(tswb 'get  temp1-123)
(display "average: ")
(display (tswb 'analytic average temp1-123))(newline)
(display "sum: ")
(tswb 'analytic sum temp1-123)
(display "standard deviation: ")
(tswb 'analytic std-dev temp1-123)
(display "minimum: ")
(tswb 'analytic minimum temp1-123)
(display "maximum: ")
(tswb 'analytic maximum temp1-123)
(display "count: ") 
(tswb 'analytic count temp1-123)
(display "product: ")
(tswb 'analytic multiply temp1-123)
(display "quotient: ")
(tswb 'analytic divide temp1-123)
(display "clearing list... ")
(tswb 'clear)(newline)
(display "is empty? ")
(tswb 'empty?)
