package team.creative.itemphysic.common;

public interface ItemEntityPhysic {
    
    public boolean skipRendering();
    
    public int age();
    
    public void age(int age);
    
    public int health();
    
    public void health(int health);
    
    public void hurted();
    
}
