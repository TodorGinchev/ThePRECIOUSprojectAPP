startx=100;
starty=100;
clf
hold on
grid on
axis equal
% for i=0:360
%     angle = 0.01*i;
%     x =  (angle)*cos(angle)*i/100000;
%     y = (angle)*sin(angle)*i/100000;
%     plot (x,y,'or')
% end
% for i=360:2*360
%     angle = 0.01*i;
%     x =  (angle)*cos(angle)*i/100000;
%     y = (angle)*sin(angle)*i/100000;
%     plot (x,y,'ob')
% end

for t=0:0.01:6*pi
    x =  (t)*cos(t)*t;
    y = (t)*sin(t)*t;
    plot (x,y,'ob')
end

% th = 0:pi/50:2*pi;
% r=30;
% x=100;
% y=100;
% xunit = r * cos(th) + startx;
% yunit = r * sin(th) + startx;
% plot(xunit,yunit)

% plot(startx, starty, '.w', 'MarkerSize',169)
            