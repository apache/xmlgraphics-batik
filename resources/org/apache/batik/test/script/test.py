print "    !!! loading script !!!"

def test(evt):
    print "    ---> mutation event test listener correctly called the function"
    print "    ---> event it " + `evt`
    print "    ---> event target is "+ `evt.getTarget()`
    evt.getTarget().setAttribute("x", "10")

print "    !!! script loaded !!!"

