puts "    !!! loading script !!!"

proc test { evt } {
        puts "    ---> mutation event test listener is correctly called"
        puts "    ---> event is [$evt toString]"
        puts "    ---> event target is [[$evt getTarget] toString]"
        set element [java::cast org.w3c.dom.Element [$evt getTarget]]
        $element setAttribute "x"  "10"
}               
        
puts "    !!! script loaded !!!"