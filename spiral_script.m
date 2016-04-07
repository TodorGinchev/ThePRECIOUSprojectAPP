startx=100;
starty=100;
clf
hold on
axis equal
for i=6*360:15*360
    angle = 0.01*i;
    x =  startx + (i/10+angle)*cos(angle)*i/10000;
    y = starty + (i/10+angle)*sin(angle)*i/10000;
    plot (x,y,'o')
end

% th = 0:pi/50:2*pi;
% r=30;
% x=100;
% y=100;
% xunit = r * cos(th) + startx;
% yunit = r * sin(th) + startx;
% plot(xunit,yunit)

% plot(startx, starty, '.w', 'MarkerSize',169)
            