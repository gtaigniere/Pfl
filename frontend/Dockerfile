ARG NODE_VERSION
ARG NG_VERSION

FROM node:${NODE_VERSION}
ENV HOME=/app
# permet l'utilisation de la commande ng
ENV PATH=$HOME/node_modules/.bin/:$PATH
RUN mkdir -p $HOME
WORKDIR $HOME
RUN npm i -g npm && npm i @angular/cli@${NG_VERSION}
CMD npm i && ng serve --host 0.0.0.0 --port 4200
