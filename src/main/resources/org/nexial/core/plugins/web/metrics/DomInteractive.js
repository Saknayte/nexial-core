metrics.DomInteractive = JSON.parse(localStorage.getItem('n')).map(x => x.domInteractive - x.startTime).pop();
