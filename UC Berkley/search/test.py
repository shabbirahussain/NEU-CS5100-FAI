'''
Created on Sep 22, 2016

@author: shabbirhussain
'''

// Kind of binary search finds position of element in array
function getPosInArr(A[1..n], p, q, elem)
    if(p==q)
        if(elem<A[p]) return p-1
        else          return p
    
    m = floor((p+q)/2)
    
    if(elem > A[m])
        pos = getPosInArr(A, m+1, q, elem)
    else
        pos = getPosInArr(A, p, m-1, elem)

function getMedian(A[1..n], B[1..n])
    mB = B[floor(n/2)]
    elemB4 = getPosInArr(A, 1, n, mB)
 

if __name__ == '__main__':
    pass