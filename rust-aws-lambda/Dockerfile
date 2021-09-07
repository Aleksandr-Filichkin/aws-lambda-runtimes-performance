FROM rust:1.54.0

# 2. Copy the files in your machine to the Docker image
COPY ./ ./

RUN rustup target add x86_64-unknown-linux-musl
RUN apt-get update

RUN apt-get install musl-tools -y

RUN apt-get -y install pkg-config libssl-dev
# Build your program for release
ENV PKG_CONFIG_ALLOW_CROSS 1
RUN cargo build --release --target x86_64-unknown-linux-musl
RUN cp target/x86_64-unknown-linux-musl/release/rust-aws-lambda ./app


RUN apt-get update
RUN  apt install zip

RUN chmod +x bootstrap
RUN chmod +x app
RUN #chmod +x ./app
RUN zip -j function.zip bootstrap app

