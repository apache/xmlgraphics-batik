System.out.println("    !!! loading script !!!");

function test(evt) {
        System.out.println("    ---> mutation event test listener correctly called the function")
        System.out.println("    ---> event is "+evt);
        System.out.println("    ---> event target is "+evt.getTarget()); 
        evt.getTarget().setAttribute("x", "10");
}               
        
System.out.println("    !!! script loaded !!!");